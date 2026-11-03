package com.liakops.ship_tracker;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionStage;

// Μπορεί να είναι inner class ή ξεχωριστή κλάση
public class AisListener implements WebSocket.Listener {

    private final String apiKey;

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
        CharBuffer characterBuffer = StandardCharsets.UTF_8.decode(data);
        System.out.println(characterBuffer.toString());
        webSocket.request(1);
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.out.println("Error:" + error.getMessage());

    }

}
