package com.liakops.ship_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Voyage data declared by the crew (only destination and ETA are used).
@JsonIgnoreProperties(ignoreUnknown = true)
public record ShipStaticData(
        @JsonProperty("Destination") String destination,
        @JsonProperty("Eta") Eta eta
) {
}
