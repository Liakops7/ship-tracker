package com.liakops.ship_tracker;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

// Μπορεί να είναι inner class ή ξεχωριστή κλάση
public class AisListener implements WebSocket.Listener {

    @Override
    public void onOpen(WebSocket webSocket) {
        // TODO: στείλε το subscription JSON -> webSocket.sendText(json, true)
        // TODO: ζήτα το πρώτο μήνυμα -> webSocket.request(1)
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        // TODO: μετάτρεψε τα bytes σε String (UTF-8) και τύπωσέ το
        // TODO: ζήτα το επόμενο μήνυμα -> webSocket.request(1)
        return null;
    }

    // TODO: υλοποίησε και onError(...) και onClose(...) ώστε να τυπώνουν τι έγινε
}
