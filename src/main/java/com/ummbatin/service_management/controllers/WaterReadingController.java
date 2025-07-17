package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.WaterReading;
import com.ummbatin.service_management.services.WaterReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/water-readings")
public class WaterReadingController {
    @Autowired
    private WaterReadingService waterReadingService;

    // Residents can view their water readings
    @GetMapping("/property/{propertyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESIDENT')")
    public List<WaterReading> getWaterReadings(@PathVariable Long propertyId) {
        return waterReadingService.getWaterReadingsByProperty(propertyId);
    }

    // Only Admin can add a water reading
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public WaterReading createWaterReading(@RequestBody WaterReading waterReading) {
        return waterReadingService.createWaterReading(waterReading);
    }
}
