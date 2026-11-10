package com.liakops.ship_tracker.dto;

import com.liakops.ship_tracker.model.Vessel;

public record VesselResponse(
        long mmsi,
        String name,
        double latitude,
        double longitude,
        String lastSeen
) {

    public static VesselResponse from(Vessel vessel) {
        return new VesselResponse(vessel.mmsi(), vessel.name(), vessel.latitude(), vessel.longitude(), AthensTime.format(vessel.lastSeen()));
    }
}
