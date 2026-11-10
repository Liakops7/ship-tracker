package com.liakops.ship_tracker.service;

import com.liakops.ship_tracker.model.Arrival;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// In-memory arrivals, one per vessel (MMSI). Newer messages replace older ones.
@Service
public class ArrivalStore {

    private final Map<Long, Arrival> arrivals = new ConcurrentHashMap<>();

    public void save(Arrival arrival) {
        arrivals.put(arrival.mmsi(), arrival);
    }

    public Collection<Arrival> findAll() {
        return arrivals.values();
    }

    // Rule 4: arrivals with ETA between now and now + window, nearest first
    public List<Arrival> findUpcoming(Duration window) {
        Instant now = Instant.now();
        Instant end = now.plus(window);

        return arrivals.values().stream()
                .filter(arrival -> arrival.eta().isAfter(now) && arrival.eta().isBefore(end))
                .sorted(Comparator.comparing(Arrival::eta))
                .toList();
    }
}
