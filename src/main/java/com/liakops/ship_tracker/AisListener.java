package com.liakops.ship_tracker;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

// Μπορεί να είναι inner class ή ξεχωριστή κλάση
public class AisListener implements WebSocket.Listener {

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("Connected");
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        // TODO: τύπωσε το statusCode και το reason
        System.out.println(statusCode);
        System.out.println(reason);

        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        // TODO: τύπωσε το error.getMessage()
        System.out.println(error.getMessage());

    }

}
