package com.liakops.ship_tracker.service;

public class PiraeusMatcher {
    public static boolean matches(String destination) {
        if (destination == null) {
            return false;
        }
        String target = destination.substring(destination.lastIndexOf('>') + 1);      // ← ΝΕΟ

        String clean = target.toUpperCase().replaceAll("[^A-Z]", "");
        return clean.startsWith("GRPIR")
                || clean.contains("PIRAEUS")
                || clean.contains("PIRAIEVS")
                || clean.contains("PIREAS");

    }
}
