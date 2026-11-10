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

@Service
public class ArrivalStore {
    private final Map<Long, Arrival> arrivals = new ConcurrentHashMap<>();

    public void save(Arrival arrival) {
        arrivals.put(arrival.mmsi(), arrival);
    }

    public Collection<Arrival> findAll() {
        return arrivals.values();
    }

    public List<Arrival> findUpcoming(Duration window){
        Instant now = Instant.now();
        Instant end = now.plus(window);

        return arrivals.values().stream()
                    // Κανόνας 4: το ETA είναι ΜΕΤΑ το τώρα ΚΑΙ ΠΡΙΝ το τέλος του παραθύρου
                    .filter(arrival -> arrival.eta().isAfter(now)
                            && arrival.eta().isBefore(end))
                    // ταξινόμηση κατά ETA: από τη νωρίτερη στην αργότερη
                    .sorted(Comparator.comparing(Arrival::eta))
                    .toList();
    }
}
