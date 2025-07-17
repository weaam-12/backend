package com.ummbatin.service_management.services;

import com.ummbatin.service_management.models.WaterReading;
import com.ummbatin.service_management.repositories.WaterReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WaterReadingService {
    @Autowired
    private WaterReadingRepository waterReadingRepository;

    public List<WaterReading> getWaterReadingsByProperty(Long propertyId) {
        return waterReadingRepository.findByProperty_PropertyId(propertyId);
    }

    public WaterReading createWaterReading(WaterReading waterReading) {
        return waterReadingRepository.save(waterReading);
    }
}