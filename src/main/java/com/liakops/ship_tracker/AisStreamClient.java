package com.liakops.ship_tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;

@Component
public class AisStreamClient implements CommandLineRunner {

    @Value("${aisstream.api-key}")
    private String apiKey;

    @Override
    public void run(String... args) throws Exception {
        URI uri = URI.create("wss://stream.aisstream.io/v0/stream");

        // Δημιουργεί έναν client με τις προεπιλεγμένες ρυθμίσεις
        HttpClient client = HttpClient.newHttpClient();

        // Ο client δεν ανοίγει WebSocket απευθείας. Σου δίνει έναν builder,
        // δηλαδή ένα αντικείμενο που ρυθμίζεις πριν φτιάξεις τη σύνδεση
        WebSocket.Builder builder = client.newWebSocketBuilder();

        /*Το buildAsync θέλει δύο ορίσματα: το URI και ένα αντικείμενο listener.
        Επιστρέφει CompletableFuture<WebSocket>, δηλαδή «μια υπόσχεση ότι
        κάποια στιγμή θα έχεις WebSocket». Με το .join() περιμένεις μέχρι
        να ανοίξει πραγματικά
        */
        AisListener aisListener = new AisListener(apiKey);
        WebSocket ws = builder.buildAsync(uri, aisListener).join();


    }
}
