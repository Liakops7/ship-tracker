package com.liakops.ship_tracker.controller;


import com.liakops.ship_tracker.model.Metadata;
import com.liakops.ship_tracker.service.VesselStore;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController // κλάση που απαντά σε HTTP requests και επιστρέφει JSON
public class VesselController {

    private VesselStore vesselStore;

    public VesselController(VesselStore vesselStore) {
            this.vesselStore = vesselStore;
    }

    public Collection<Metadata> getAegeanVessels(){

        return java.util.List.of();
    }

}
