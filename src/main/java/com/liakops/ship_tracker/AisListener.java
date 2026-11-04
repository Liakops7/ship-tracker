package com.liakops.ship_tracker;

import com.liakops.ship_tracker.model.AisMessage;
import com.liakops.ship_tracker.model.Metadata;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionStage;

// Μπορεί να είναι inner class ή ξεχωριστή κλάση
public class AisListener implements WebSocket.Listener {

    private final String apiKey;
    private final JsonMapper mapper = JsonMapper.builder().build();

    public AisListener(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("Connected");

        String aegeanBox = "[ [ [35.0, 22.5], [41.0, 28.5] ] ]";
        String json = """
                   {"APIKey": "%s", "BoundingBoxes": %s, "FilterMessageTypes": ["%s"]}
                """.formatted(apiKey, aegeanBox, "PositionReport");

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

                // 5. Τύπωσε μια καθαρή γραμμή — trim() για να φύγουν τα κενά από το όνομα
                System.out.println(meta.shipName().trim()
                        + " (" + meta.mmsi() + ") @ "
                        + meta.latitude() + ", " + meta.longitude()
                        + " — " + meta.timeUTC());
            }
        } catch (JacksonException e) {
            // Αν ένα μήνυμα είναι χαλασμένο, το λέμε και συνεχίζουμε.
            // Χωρίς try/catch, ένα κακό μήνυμα θα έκλεινε όλη τη σύνδεση.
            System.out.println("Could not parse message: " + e.getMessage());
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
