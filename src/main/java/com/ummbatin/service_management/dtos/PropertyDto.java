package com.ummbatin.service_management.dtos;

import com.ummbatin.service_management.models.Property;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PropertyDto {
    private Long propertyId;
    private String address;
    private BigDecimal area;
    private Integer numberOfUnits;

    // Constructor that takes Property entity
    public PropertyDto(Property property) {
        this.propertyId = property.getPropertyId();
        this.address = property.getAddress();
        this.area = property.getArea();
        this.numberOfUnits = property.getNumberOfUnits();
    }

    // Method to convert DTO to Entity
    public Property toEntity() {
        Property property = new Property();
        property.setPropertyId(this.propertyId);
        property.setAddress(this.address);
        property.setArea(this.area);
        property.setNumberOfUnits(this.numberOfUnits);
        return property;
    }
}