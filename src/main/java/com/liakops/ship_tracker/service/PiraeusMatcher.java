package com.liakops.ship_tracker.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

// Decides whether a crew-entered destination means the port of Piraeus.
// Rules are based on real AISStream data.
public class PiraeusMatcher {

    // Words that may follow the port name, e.g. "PIRAEUS GRE", "PIRAEUS ANCH"
    private static final Set<String> SUFFIXES = Set.of("GR", "GRE", "GREECE", "ANCH", "ANCHORAGE");

    public static boolean matches(String destination) {
        if (destination == null) {
            return false;
        }

        // "FROM>TO": keep only the part after the last '>'
        String target = destination.substring(destination.lastIndexOf('>') + 1);

        // Split into words on anything that is not a letter
        List<String> words = new ArrayList<>(Arrays.asList(target.toUpperCase().split("[^A-Z]+")));
        words.removeIf(String::isEmpty);

        // Drop trailing suffix words
        while (!words.isEmpty() && SUFFIXES.contains(words.get(words.size() - 1))) {
            words.remove(words.size() - 1);
        }

        if (words.isEmpty()) {
            return false;
        }

        // Only the last word (final destination) counts:
        // "HER-SER-PIR" -> true, "PIR-CHIOS-MJT" -> false
        String last = words.get(words.size() - 1);

        return last.equals("PIR")             // "GR PIR", "GR>PIR"
                || last.equals("GRPIR")       // UN/LOCODE
                || last.startsWith("PIRAE")   // PIRAEUS, PIRAEUSS
                || last.startsWith("PIRE");   // PIREAS, PIREUS
    }
}
