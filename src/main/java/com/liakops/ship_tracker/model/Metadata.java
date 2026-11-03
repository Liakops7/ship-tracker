package com.liakops.ship_tracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Metadata(
    @JsonProperty("MMSI") long mmsi,
    @JsonProperty("ShipName") long shipName,
    @JsonProperty("latitude") double latitude,
    @JsonProperty("longitude") double longitude,
    @JsonProperty("time_utc") String timeUTC
) {}
