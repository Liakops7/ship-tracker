package com.liakops.ship_tracker.controller;

import com.liakops.ship_tracker.dto.ArrivalResponse;
import com.liakops.ship_tracker.service.ArrivalStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

@RestController
public class ArrivalController {

    private final ArrivalStore arrivalStore;

    // Constructor injection: το Spring δίνει μόνο του το ArrivalStore
    public ArrivalController(ArrivalStore arrivalStore) {
        this.arrivalStore = arrivalStore;
    }

    // Οι αναμενόμενες αφίξεις στον Πειραιά τις επόμενες 24 ώρες,
    // ταξινομημένες από την πιο κοντινή
    @GetMapping("/ports/piraeus/arrivals")
    public List<ArrivalResponse> getPiraeusArrivals() {
        return arrivalStore.findUpcoming(Duration.ofHours(24))
                .stream()
                .map(ArrivalResponse::from)      // Arrival → ArrivalResponse (ETA σε ώρα Αθήνας)
                .toList();
    }

    // ΠΡΟΣΩΡΙΝΟ, μόνο για δοκιμή: ΟΛΕΣ οι αποθηκευμένες αφίξεις, χωρίς το φίλτρο των 24 ωρών.
    // Θα το σβήσουμε πριν το merge.
    @GetMapping("/ports/piraeus/arrivals/all")
    public List<ArrivalResponse> getAllPiraeusArrivals() {
        return arrivalStore.findAll()
                .stream()
                .map(ArrivalResponse::from)
                .toList();
    }
}