// File: PropertyDto.java
package com.ummbatin.service_management.dtos;

import com.ummbatin.service_management.models.Property;

import java.math.BigDecimal;

public class PropertyDto {

    private Long propertyId;
    private String address;
    private BigDecimal area;
    private int numberOfUnits;

    public PropertyDto(Property property) {
        this.propertyId     = property.getPropertyId();
        this.address        = property.getAddress();
        this.area           = property.getArea();
        this.numberOfUnits  = property.getNumberOfUnits();
    }

    /* ────────────────────────────────
       Getters & Setters
       ──────────────────────────────── */
    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }

    public int getNumberOfUnits() { return numberOfUnits; }
    public void setNumberOfUnits(int numberOfUnits) { this.numberOfUnits = numberOfUnits; }
}