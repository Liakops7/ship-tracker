package com.liakops.ship_tracker;

import com.liakops.ship_tracker.model.*;
import com.liakops.ship_tracker.service.ArrivalStore;
import com.liakops.ship_tracker.service.PiraeusMatcher;
import com.liakops.ship_tracker.service.VesselStore;
import tools.jackson.databind.json.JsonMapper;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.CompletionStage;

// Receives AISStream messages and routes them to the matching store.
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

        // Subscribe to Aegean Sea messages (must be sent within 3 seconds)
        String aegeanBox = "[ [ [35.0, 22.5], [41.0, 28.5] ] ]";
        String json = """
                {"APIKey": "%s", "BoundingBoxes": %s, "FilterMessageTypes": ["PositionReport", "ShipStaticData"]}
                """.formatted(apiKey, aegeanBox);
        System.out.println(json.replace(apiKey, "***")); // never log the API key

        webSocket.sendText(json, true);
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.println("Status code: " + statusCode);
        System.out.println("Reason: " + reason);
        return null;
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        // AISStream sends JSON as binary frames
        String json = StandardCharsets.UTF_8.decode(data).toString();

        try {
            AisMessage message = mapper.readValue(json, AisMessage.class);

            if ("PositionReport".equals(message.messageType())) {
                Vessel vessel = Vessel.from(message.metaData());
                vesselStore.save(vessel);

                System.out.println(vessel.name()
                        + " (" + vessel.mmsi() + ") @ "
                        + vessel.latitude() + ", " + vessel.longitude()
                        + " — " + vessel.lastSeen());

            } else if ("ShipStaticData".equals(message.messageType())) {
                ShipStaticData staticData = message.payload().shipStaticData();
                Eta eta = staticData.eta();

                // Rules 1-2: destination is Piraeus and the ETA is available
                if (PiraeusMatcher.matches(staticData.destination())
                        && eta != null && eta.isValid()) {

                    // Rule 3: ETA in the current year (null if the date does not exist)
                    Instant etaInstant = eta.resolve(Instant.now());

                    if (etaInstant != null) {
                        arrivalStore.save(Arrival.from(message.metaData(), staticData, etaInstant));
                    }
                }
            }
        } catch (RuntimeException e) {
            // Skip a bad message instead of closing the connection
            System.out.println("Problem: " + e.getMessage());
        }

        // Always request the next message, even after an error
        webSocket.request(1);
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.out.println("Error: " + error.getMessage());
    }
}
