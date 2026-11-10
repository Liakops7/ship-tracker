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

    public ArrivalController(ArrivalStore arrivalStore) {
        this.arrivalStore = arrivalStore;
    }

    // Expected arrivals at Piraeus in the next 24 hours, nearest first
    @GetMapping("/ports/piraeus/arrivals")
    public List<ArrivalResponse> getPiraeusArrivals() {
        return arrivalStore.findUpcoming(Duration.ofHours(24))
                .stream()
                .map(ArrivalResponse::from)
                .toList();
    }
}
