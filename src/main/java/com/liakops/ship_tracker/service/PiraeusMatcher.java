package com.liakops.ship_tracker.service;

public class PiraeusMatcher {
    public static boolean matches (String destination){
        if (destination == null) {
            return false;
        }
        String clean = destination.toUpperCase().replaceAll("[^A-Z]", "");
        return clean.startsWith("GRPIR")
                || clean.contains("PIRAEUS")
                || clean.contains("PIREAS");

    }
}
