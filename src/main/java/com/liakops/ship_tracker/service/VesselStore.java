package com.liakops.ship_tracker.service;

import com.liakops.ship_tracker.model.Metadata;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VesselStore {
    private final Map<Long, Metadata> vessels = new ConcurrentHashMap<>();

    public void save (Metadata metaData) {
        vessels.put(metaData.mmsi(), metaData);
    }

    public Collection<Metadata> findAll() {
        return vessels.values();
    }
}
