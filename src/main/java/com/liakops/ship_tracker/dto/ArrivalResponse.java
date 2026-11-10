package com.liakops.ship_tracker.dto;

import com.liakops.ship_tracker.model.Arrival;

// API response for an arrival, with the ETA in Athens local time.
public record ArrivalResponse(
        long mmsi,
        String name,
        String destination,
        String eta
) {
    public static ArrivalResponse from(Arrival arrival) {
        return new ArrivalResponse(
                arrival.mmsi(),
                arrival.name(),
                arrival.destination(),
                AthensTime.format(arrival.eta())
        );
    }
}
