package com.liakops.ship_tracker.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

// Αποφασίζει αν ο δηλωμένος προορισμός ενός πλοίου είναι το λιμάνι του Πειραιά.
// Οι κανόνες βγήκαν από πραγματικά δεδομένα (ais-raw.csv).
public class PiraeusMatcher {

    // Λέξεις που μπορεί να ΑΚΟΛΟΥΘΟΥΝ το λιμάνι χωρίς να είναι λιμάνι
    // π.χ. "PIRAEUS GRE", "PIRAEUS, GREECE", "PIRAEUS ANCH"
    private static final Set<String> SUFFIXES = Set.of("GR", "GRE", "GREECE", "ANCH", "ANCHORAGE");

    public static boolean matches(String destination) {
        if (destination == null) {
            return false;
        }

        // 1. "από>προς": κρατάμε μόνο ό,τι είναι μετά το τελευταίο '>'
        //    "GRPIR>GRELE" → "GRELE",  "GR>PIR" → "PIR"
        String target = destination.substring(destination.lastIndexOf('>') + 1);

        // 2. Χωρίζουμε σε λέξεις: ό,τι δεν είναι γράμμα (κενό, παύλα, κόμμα, _) είναι διαχωριστικό
        //    "HER-SER-PIR" → [HER, SER, PIR],  "PIRAEUS, GREECE" → [PIRAEUS, GREECE]
        List<String> words = new ArrayList<>(Arrays.asList(target.toUpperCase().split("[^A-Z]+")));

        // Το split αφήνει κενή "λέξη" αν το κείμενο ξεκινά με διαχωριστικό (π.χ. " HER-SER-PIR")
        words.removeIf(String::isEmpty);

        // 3. Πετάμε από το ΤΕΛΟΣ τις λέξεις-συμπληρώματα
        //    [PIRAEUS, GREECE] → [PIRAEUS]
        while (!words.isEmpty() && SUFFIXES.contains(words.get(words.size() - 1))) {
            words.remove(words.size() - 1);
        }

        // Αν δεν έμεινε τίποτα (κενός προορισμός), δεν είναι Πειραιάς
        if (words.isEmpty()) {
            return false;
        }

        // 4. Μετράει ΜΟΝΟ η τελευταία λέξη (ο τελικός προορισμός)
        //    "PIR-CHIOS-MJT" → MJT → false (φεύγει ΑΠΟ Πειραιά)
        //    "HER-SER-PIR"   → PIR → true  (έρχεται ΣΤΟΝ Πειραιά)
        String last = words.get(words.size() - 1);

        return last.equals("PIR")             // κωδικός λιμανιού μόνος του ("GR PIR", "GR>PIR")
                || last.equals("GRPIR")       // UN/LOCODE ενωμένο
                || last.startsWith("PIRAE")   // PIRAEUS, PIRAEUSS (ορθογραφικό λάθος)
                || last.startsWith("PIRE");   // PIREAS, PIREUS
    }
}