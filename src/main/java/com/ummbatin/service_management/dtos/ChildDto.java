package com.ummbatin.service_management.dtos;

import lombok.Data;
@Data
public class ChildDto {
    private Integer childId;
    private String name;
    private String birthDate;
    private String MotherName ;

    public String getMotherName() {
        return MotherName;
    }
}

