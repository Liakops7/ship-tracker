package com.liakops.ship_tracker.model;

import java.time.Instant;

public record Arrival() {
        long mmsi = meta.mmsi();
        String name = meta.shipName().trim();
        String destination = data.destination().trim();
        Instant eta = staticData.eta().resolve(Instant.now());

    public static Arrival from(Metadata meta, ShipStaticData data, Instant eta) {
        return new Arrival(

         );
    }}
