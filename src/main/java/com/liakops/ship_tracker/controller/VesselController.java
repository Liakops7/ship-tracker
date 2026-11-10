package com.liakops.ship_tracker.controller;

import com.liakops.ship_tracker.dto.VesselResponse;
import com.liakops.ship_tracker.service.VesselStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

@RestController
public class VesselController {

    private final VesselStore vesselStore;

    public VesselController(VesselStore vesselStore) {
        this.vesselStore = vesselStore;
    }

    // Vessels in the Aegean that reported a position in the last 30 minutes
    @GetMapping("/vessels/aegean")
    public List<VesselResponse> getAegeanVessels() {
        return vesselStore.findRecent(Duration.ofMinutes(30))
                .stream()
                .map(VesselResponse::from)
                .toList();
    }
}
