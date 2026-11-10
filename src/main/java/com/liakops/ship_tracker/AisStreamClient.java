package com.liakops.ship_tracker;

import com.liakops.ship_tracker.service.ArrivalStore;
import com.liakops.ship_tracker.service.VesselStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;

// Opens the AISStream WebSocket connection when the application starts.
@Component
public class AisStreamClient implements CommandLineRunner {

    @Value("${aisstream.api-key}")
    private String apiKey;

    private final VesselStore vesselStore;
    private final ArrivalStore arrivalStore;

    public AisStreamClient(VesselStore vesselStore, ArrivalStore arrivalStore) {
        this.vesselStore = vesselStore;
        this.arrivalStore = arrivalStore;
    }

    @Override
    public void run(String... args) {
        URI uri = URI.create("wss://stream.aisstream.io/v0/stream");
        AisListener listener = new AisListener(apiKey, vesselStore, arrivalStore);

        // join() waits until the connection is open
        HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(uri, listener)
                .join();
    }
}
