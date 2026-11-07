package com.liakops.ship_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ShipStaticData(
        @JsonProperty("Destination") String destination,
        @JsonProperty("Eta") Eta eta
) {
}
