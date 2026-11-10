package com.liakops.ship_tracker.service;

import com.liakops.ship_tracker.model.Arrival;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

}
