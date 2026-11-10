package com.liakops.ship_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

// AIS ETA in UTC. AIS sends no year.
@JsonIgnoreProperties(ignoreUnknown = true)
public record Eta(
        @JsonProperty("Month") int month,
        @JsonProperty("Day") int day,
        @JsonProperty("Hour") int hour,
        @JsonProperty("Minute") int minute
) {
    // AIS uses month 0, day 0, hour 24, minute 60 for "not available"
    public boolean isValid() {
        return month >= 1 && month <= 12
                && day >= 1 && day <= 31
                && hour >= 0 && hour <= 23
                && minute >= 0 && minute <= 59;
    }

    private Instant atYear(int year) {
        return LocalDateTime.of(year, month, day, hour, minute)
                .toInstant(ZoneOffset.UTC);
    }

    // ETA in the current year, or null if the date does not exist (e.g. 31 February)
    public Instant resolve(Instant now) {
        int currentYear = now.atZone(ZoneOffset.UTC).getYear();
        try {
            return atYear(currentYear);
        } catch (DateTimeException e) {
            return null;
        }
    }
}
