package com.liakops.ship_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AisPayload(
        @JsonProperty("ShipStaticData") ShipStaticData shipStaticData
) {
}
