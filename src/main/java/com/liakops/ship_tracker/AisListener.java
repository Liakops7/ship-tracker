package com.liakops.ship_tracker;

import com.liakops.ship_tracker.model.*;
import com.liakops.ship_tracker.service.ArrivalStore;
import com.liakops.ship_tracker.service.PiraeusMatcher;
import com.liakops.ship_tracker.service.VesselStore;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.concurrent.CompletionStage;

// Μπορεί να είναι inner class ή ξεχωριστή κλάση
public class AisListener implements WebSocket.Listener {

    private final String apiKey;
    private final JsonMapper mapper = JsonMapper.builder().build();
    private final VesselStore vesselStore;
    private final ArrivalStore arrivalStore;

    public AisListener(String apiKey, VesselStore vesselStore, ArrivalStore arrivalStore) {

        this.apiKey = apiKey;
        this.vesselStore = vesselStore;
        this.arrivalStore = arrivalStore;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("Connected");

        String aegeanBox = "[ [ [35.0, 22.5], [41.0, 28.5] ] ]";
        String json = """
                   {"APIKey": "%s", "BoundingBoxes": %s, "FilterMessageTypes": ["PositionReport", "ShipStaticData"]}
                """.formatted(apiKey, aegeanBox);
        System.out.println(json.replace(apiKey, "***"));   // τυπώνει το subscription ΧΩΡΙΣ το key
        webSocket.sendText(json, true);
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.println("Status code:" + statusCode);
        System.out.println("Reason:" + reason);

        return null;
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        // 1. bytes -> String (αυτό το είχες ήδη)
        String json = StandardCharsets.UTF_8.decode(data).toString();

        try {
            // 2. String -> αντικείμενο AisMessage
            //    "Διάβασε αυτό το JSON και φτιάξε μου ένα AisMessage"
            AisMessage message = mapper.readValue(json, AisMessage.class);

            // 3. Κράτα μόνο τα μηνύματα θέσης (η επιβεβαίωση subscription δεν μας ενδιαφέρει)
            //    Γράφουμε "PositionReport".equals(...) και όχι ανάποδα,
            //    ώστε να μη σκάσει αν το messageType είναι null
            if ("PositionReport".equals(message.messageType())) {

                // 4. Πάρε το εσωτερικό αντικείμενο με τα στοιχεία του πλοίου
                Metadata meta = message.metaData();

                Vessel vessel = Vessel.from(meta);
                vesselStore.save(vessel);

                System.out.println(vessel.name()
                        + " (" + vessel.mmsi() + ") @ "
                        + vessel.latitude() + ", " + vessel.longitude()
                        + " — " + vessel.lastSeen());
            } else if ("ShipStaticData".equals(message.messageType())) {
                // Τα στατικά στοιχεία: φάκελος → φορτίο → ShipStaticData
                ShipStaticData staticData = message.payload().shipStaticData();


                // ΠΡΟΣΩΡΙΝΟ: τι γράφουν ως προορισμό τα πλοία της ακτοπλοΐας;
                String shipName = message.metaData().shipName();
                if (shipName.contains("BLUE STAR") || shipName.contains("POSEIDONAS")
                        || shipName.contains("OKIALOS") || shipName.contains("AERO")) {
                    System.out.println("DEBUG: " + shipName.trim()
                            + " → [" + staticData.destination() + "] " + staticData.eta());
                }

                // Κανόνας 1: ο προορισμός είναι Πειραιάς
                // Κανόνας 2: το ETA δεν είναι "μη διαθέσιμο" (μήνας 0, ώρα 24 κ.λπ.)
                if (PiraeusMatcher.matches(staticData.destination())
                        && staticData.eta().isValid()) {

                    // Κανόνας 3: οι 4 αριθμοί του ETA → πραγματική στιγμή, στο τρέχον έτος
                    Instant eta = staticData.eta().resolve(Instant.now());

                    // Το resolve επιστρέφει null αν η ημερομηνία δεν υπάρχει (π.χ. 31 Φεβρουαρίου)
                    if (eta != null) {
                        // Από τα 3 κομμάτια (φάκελος, στατικά στοιχεία, ETA) → το δικό μας Arrival
                        Arrival arrival = Arrival.from(message.metaData(), staticData, eta);

                        // Αποθήκευση: αν υπάρχει ήδη αυτό το MMSI, το νέο αντικαθιστά το παλιό
                        arrivalStore.save(arrival);

                        // Προσωρινό print για να βλέπουμε ότι δουλεύει
                        System.out.println("ARRIVAL: " + arrival);
                    }
                }
            }
        } catch (DateTimeParseException | JacksonException e) {
            // Αν ένα μήνυμα είναι χαλασμένο, το λέμε και συνεχίζουμε.
            // Χωρίς try/catch, ένα κακό μήνυμα θα έκλεινε όλη τη σύνδεση.
            System.out.println("Problem: " + e.getMessage());
        }


        // 6. Ζήτα το επόμενο μήνυμα — ΕΞΩ από το try, ώστε να γίνεται πάντα
        webSocket.request(1);
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.out.println("Error:" + error.getMessage());

    }

}
