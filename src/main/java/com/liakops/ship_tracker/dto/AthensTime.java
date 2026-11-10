package com.liakops.ship_tracker.dto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class AthensTime {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        private static final ZoneId ATHENS = ZoneId.of("Europe/Athens");

        public static String format(Instant instant) {
            ZonedDateTime athensTime = instant.atZone(ATHENS);

            String dateTime = athensTime.format(DISPLAY_FORMAT);

            int offsetHours = athensTime.getOffset().getTotalSeconds() / 3600;

            return dateTime + " UTC +" + offsetHours;
        }
}
