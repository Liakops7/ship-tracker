package com.liakops.ship_tracker.service;

import com.liakops.ship_tracker.model.Eta;
import com.liakops.ship_tracker.model.Metadata;
import com.liakops.ship_tracker.model.ShipStaticData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// Γράφει τα ΩΜΑ δεδομένα κάθε μηνύματος ShipStaticData σε αρχείο CSV,
// για να τα μελετήσουμε στο Excel (π.χ. τι γράφουν τα πλοία ως προορισμό).
@Service
public class RawDataLogger {

    // Το αρχείο δημιουργείται στον φάκελο του project (δίπλα στο pom.xml)
    private static final Path FILE = Path.of("ais-raw.csv");

    // Μία γραμμή στο CSV για κάθε μήνυμα
    public void log(Metadata meta, ShipStaticData data) {
        Eta eta = data.eta();

        String line = quote(meta.timeUTC())               // πότε ελήφθη
                + "," + meta.mmsi()                       // ταυτότητα πλοίου
                + "," + quote(meta.shipName())            // όνομα, ΩΜΟ (με τα κενά)
                + "," + meta.latitude()                   // θέση
                + "," + meta.longitude()
                + "," + quote(data.destination())         // προορισμός, ΩΜΟΣ (χωρίς trim)
                + "," + eta.month()                       // ETA όπως έρχεται (και τα 0/24/60)
                + "," + eta.day()
                + "," + eta.hour()
                + "," + eta.minute()
                + "\n";                                   // αλλαγή γραμμής

        try {
            // CREATE = φτιάξε το αρχείο αν δεν υπάρχει
            // APPEND = γράψε στο ΤΕΛΟΣ, χωρίς να σβήσεις τις παλιές γραμμές
            Files.writeString(FILE, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Το IOException είναι checked: η Java μας ΥΠΟΧΡΕΩΝΕΙ να το πιάσουμε.
            // Αν αποτύχει η εγγραφή, το λέμε και συνεχίζουμε (δεν κόβουμε τη σύνδεση).
            System.out.println("CSV error: " + e.getMessage());
        }
    }

    // Βάζει ένα κείμενο σε εισαγωγικά για CSV, ώστε τα κόμματα μέσα του
    // (π.χ. "PIRAEUS,GREECE") να μη "σπάνε" τις στήλες στο Excel.
    // Αν το κείμενο έχει ήδη εισαγωγικά, τα διπλασιάζουμε (κανόνας του CSV).
    private static String quote(String text) {
        if (text == null) {
            return "";
        }
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}