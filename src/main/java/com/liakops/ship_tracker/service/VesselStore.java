package com.liakops.ship_tracker.service;

import com.liakops.ship_tracker.model.Metadata;
import com.liakops.ship_tracker.model.Vessel;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VesselStore {
    private final Map<Long, Vessel> vessels = new ConcurrentHashMap<>();

    public void save (Vessel vessel) {
        vessels.put(vessel.mmsi(), vessel);
    }

    public Collection<Vessel> findAll() {
        return vessels.values();
    }
}
