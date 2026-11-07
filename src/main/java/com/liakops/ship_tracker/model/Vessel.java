package com.liakops.ship_tracker.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public record Vessel(
        long mmsi,
        String name,
        double latitude,
        double longitude,
        Instant lastSeen
) {
    private static final DateTimeFormatter AIS_TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .appendPattern(" Z")
            .toFormatter();

    private static Instant parseAisTime(String aisTime) {
        String clean = aisTime.replace(" UTC", "");
        return OffsetDateTime.parse(clean, AIS_TIME_FORMAT).toInstant();

    }

    public static Vessel from(Metadata meta) {
        return new Vessel(meta.mmsi(), meta.shipName().trim(), meta.latitude(), meta.longitude(), parseAisTime(meta.timeUTC()));
    }
}
