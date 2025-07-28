package com.ummbatin.service_management.dtos;

import lombok.Data;
@Data
public class ChildDto {
    private Integer childId;
    private String name;
    private String birthDate;
    private String MotherName ;
    private UserDto user; // Add this field

    public String getMotherName() {
        return MotherName;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}

