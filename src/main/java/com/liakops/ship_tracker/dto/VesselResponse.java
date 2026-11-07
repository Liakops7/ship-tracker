package com.liakops.ship_tracker.dto;

import com.liakops.ship_tracker.model.Vessel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public record VesselResponse(
        long mmsi,
        String name,
        double latitude,
        double longitude,
        String lastSeen
) {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH/mm/ss");

    private static final ZoneId ATHENS = ZoneId.of("Europe/Athens");

    private static String formatForAthens(Instant instant) {
        ZonedDateTime athensTime = instant.atZone(ATHENS);

        String dateTime = athensTime.format(DISPLAY_FORMAT);

        int offsetHours = athensTime.getOffset().getTotalSeconds();

        return dateTime + "UTC +" + offsetHours;
    }

    public static VesselResponse from(Vessel vessel) {
        return new VesselResponse(
            long mmsi,
            String name,
            double latitude,
            double longitude,
            String lastSeen.formatForAthens()
        )
    }
}
