package com.liakops.ship_tracker.model;

import java.time.Instant;

public record Arrival(
        long mmsi,
        String name,
        String destination,
        Instant eta
    ){
    public static Arrival from(Metadata meta, ShipStaticData data, Instant eta) {
        return new Arrival(
                meta.mmsi(),
                meta.shipName().trim(),
                data.destination().trim(),
                data.eta().resolve(Instant.now()
         );
    }}
