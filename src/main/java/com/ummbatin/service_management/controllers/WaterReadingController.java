package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.Property;
import com.ummbatin.service_management.models.WaterReading;
import com.ummbatin.service_management.services.PropertyService;
import com.ummbatin.service_management.services.WaterReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/water-readings")
public class WaterReadingController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private WaterReadingService waterReadingService;

    // Residents can view their water readings
    @GetMapping("/property/{propertyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESIDENT')")
    public List<WaterReading> getWaterReadings(@PathVariable Long propertyId) {
        return waterReadingService.getWaterReadingsByProperty(propertyId);
    }


    @PostMapping("/by-property")
    @PreAuthorize("hasRole('ADMIN')")
    public WaterReading createWaterReading(@RequestBody Map<String, Object> payload) {
        Long propertyId = Long.valueOf(payload.get("propertyId").toString());
        Double amount = Double.valueOf(payload.get("amount").toString());

        WaterReading waterReading = new WaterReading();
        waterReading.setAmount(amount);
        waterReading.setDate(LocalDateTime.now());

        // احصل على Property من الخدمة
        Property property = propertyService.getPropertyById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        waterReading.setProperty(property);

        return waterReadingService.createWaterReading(waterReading);
    }

    // Only Admin can add a water reading
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public WaterReading createWaterReading(@RequestBody WaterReading waterReading) {
        return waterReadingService.createWaterReading(waterReading);
    }
}
