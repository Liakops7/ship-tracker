package com.liakops.ship_tracker.service;

import com.liakops.ship_tracker.model.Arrival;
import com.liakops.ship_tracker.model.Vessel;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class ArrivalStore {
    Map<Long, Arrival> arrivals;

    public void save(Arrival arrival){
        arrivals.put(arrival.mmsi(), arrival);
    }

    public Collection<Arrival> findAll() {
        return arrivals.values();
    }

}
