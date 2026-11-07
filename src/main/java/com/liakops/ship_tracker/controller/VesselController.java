package com.liakops.ship_tracker.controller;


import com.liakops.ship_tracker.model.Vessel;
import com.liakops.ship_tracker.service.VesselStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController // κλάση που απαντά σε HTTP requests και επιστρέφει JSON
public class VesselController {

    private final VesselStore vesselStore;

    public VesselController(VesselStore vesselStore) {
            this.vesselStore = vesselStore;
    }

    @GetMapping("/vessels/aegean")
    public Collection<Vessel> getAegeanVessels(){

        return vesselStore.findAll();
    }

}
