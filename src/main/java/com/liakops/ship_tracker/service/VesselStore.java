package com.liakops.ship_tracker.service;

import com.liakops.ship_tracker.model.Vessel;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// In-memory latest position per vessel (MMSI). Thread-safe: written by the
// WebSocket thread, read by web request threads.
@Service
public class VesselStore {

    private final Map<Long, Vessel> vessels = new ConcurrentHashMap<>();

    public void save(Vessel vessel) {
        vessels.put(vessel.mmsi(), vessel);
    }

    public Collection<Vessel> findAll() {
        return vessels.values();
    }

    // Vessels that reported a position within maxAge
    public List<Vessel> findRecent(Duration maxAge) {
        Instant cutoff = Instant.now().minus(maxAge);

        return vessels.values().stream()
                .filter(vessel -> vessel.lastSeen().isAfter(cutoff))
                .toList();
    }
}
