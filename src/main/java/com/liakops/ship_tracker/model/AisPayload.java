package com.liakops.ship_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// The "Message" part of an AISStream message (only ShipStaticData is used).
@JsonIgnoreProperties(ignoreUnknown = true)
public record AisPayload(
        @JsonProperty("ShipStaticData") ShipStaticData shipStaticData
) {
}
