package com.liakops.ship_tracker.controller;


import com.liakops.ship_tracker.dto.VesselResponse;
import com.liakops.ship_tracker.model.Vessel;
import com.liakops.ship_tracker.service.VesselStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // κλάση που απαντά σε HTTP requests και επιστρέφει JSON
public class VesselController {

    private final VesselStore vesselStore;

    public VesselController(VesselStore vesselStore) {
            this.vesselStore = vesselStore;
    }

    @GetMapping("/vessels/aegean")
    public List<VesselResponse> getAegeanVessels(){

        return vesselStore.findAll()
                .stream()
                .map(Vessel -> from(Vessel))
                .toList();
    }

}
