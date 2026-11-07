package com.liakops.ship_tracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record Vessel (
        long mmsi,
        String shipName,
        double latitude,
        double longitude,
        Instant lastSeen

){}
