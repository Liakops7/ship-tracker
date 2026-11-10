package com.liakops.ship_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.*;

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
                hour >= 0 &&
                hour <= 23 &&
                minute >= 0 &&
                minute <= 59;


        return flag;
    }

    private Instant atYear(int year) {
        return LocalDateTime.of(year, month, day, hour, minute)   // έτος από την παράμετρο, τα υπόλοιπα από τα πεδία του record
                    .toInstant(ZoneOffset.UTC);
    }

    public Instant resolve(Instant now){
        int currentYear = now.atZone(ZoneOffset.UTC).getYear();

        try {
            return atYear(currentYear);
        }
        catch (DateTimeException e) {
            return null;
        }
    }
}
