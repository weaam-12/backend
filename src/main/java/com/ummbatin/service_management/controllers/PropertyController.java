package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.dtos.PropertyDto;
import com.ummbatin.service_management.models.Property;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.repositories.UserRepository;
import com.ummbatin.service_management.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Property> getPropertiesByUser(@PathVariable Long userId) {
        return propertyService.getPropertiesByUserId(userId);
    }
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Property addNewProperty(@RequestBody PropertyDto dto) {
        // convert DTO -> entity, then delegate to service
        return propertyService.createProperty(dto.toEntity());
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Property createProperty(@RequestBody Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "المستخدم غير موجود"));

        Property property = new Property();
        property.setAddress((String) payload.get("address"));
        property.setArea(new BigDecimal(payload.get("area").toString()));
        property.setNumberOfUnits((Integer) payload.get("numberOfUnits"));
        property.setUser(user);

        return propertyService.createProperty(property);
    }
}