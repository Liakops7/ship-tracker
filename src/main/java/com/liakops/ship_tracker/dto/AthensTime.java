package com.liakops.ship_tracker.dto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// Formats an Instant as Athens local time, e.g. "10/11/2026 14:30:00 UTC +2".
public class AthensTime {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final ZoneId ATHENS = ZoneId.of("Europe/Athens");

    public static String format(Instant instant) {
        ZonedDateTime athensTime = instant.atZone(ATHENS);
        String dateTime = athensTime.format(DISPLAY_FORMAT);

        // +2 in winter, +3 in summer (daylight saving time)
        int offsetHours = athensTime.getOffset().getTotalSeconds() / 3600;

        return dateTime + " UTC +" + offsetHours;
    }
}
