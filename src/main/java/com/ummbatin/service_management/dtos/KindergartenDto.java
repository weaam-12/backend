package com.ummbatin.service_management.dtos;

import java.util.List;

public class KindergartenDto {
    private Integer kindergartenId;
    private String name;
    private Integer capacity;
    private String location;
    private List<ChildDto> children;

    // Getters and Setters
    public Integer getKindergartenId() {
        return kindergartenId;
    }

    public void setKindergartenId(Integer kindergartenId) {
        this.kindergartenId = kindergartenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<ChildDto> getChildren() {
        return children;
    }

    public void setChildren(List<ChildDto> children) {
        this.children = children;
    }
}