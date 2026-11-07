package com.liakops.ship_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Eta(
        @JsonProperty("Month") int month,
        @JsonProperty("Day") int day,
        @JsonProperty("Hour") int hour,
        @JsonProperty("Minute") int minute
) {
    public boolean isValid() {
        boolean flag = month >= 1 &&
                month <= 12 &&
                day >= 1 &&
                day <= 31 &&
                hour >= 1 &&
                hour <= 24 &&
                minute >= 1 &&
                minute <= 60;



        return flag;
    }
}
