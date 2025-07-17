package com.ummbatin.service_management.services;

import com.ummbatin.service_management.models.Property;
import com.ummbatin.service_management.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;

    public List<Property> getPropertiesByUserId(Long userId) {
        return propertyRepository.findByUser_UserId(userId);
    }

    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }
}