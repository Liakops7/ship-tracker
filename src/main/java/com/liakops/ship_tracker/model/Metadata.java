package com.liakops.ship_tracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Metadata() {
    @JsonProperty("MMSI") long MMSI,
    @JsonProperty("latitude") double latitude,
    @JsonProperty("longitude") double longitude,
    @JsonProperty("time_UTC") String timeUTC

}
