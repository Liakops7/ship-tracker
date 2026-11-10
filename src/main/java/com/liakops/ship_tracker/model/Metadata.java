package com.liakops.ship_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// The "MetaData" part of an AISStream message (same for every message type).
@JsonIgnoreProperties(ignoreUnknown = true)
public record Metadata(
        @JsonProperty("MMSI") long mmsi,
        @JsonProperty("ShipName") String shipName,
        @JsonProperty("latitude") double latitude,
        @JsonProperty("longitude") double longitude,
        @JsonProperty("time_utc") String timeUTC
) {
}
