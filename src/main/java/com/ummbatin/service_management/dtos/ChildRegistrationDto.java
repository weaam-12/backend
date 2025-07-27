package com.ummbatin.service_management.dtos;

import lombok.Data;

@Data
public class ChildRegistrationDto {
    private String name;
    private String birthDate;
    private String motherName;
    private int wifeIndex;

    public int getWifeIndex() {
       return this.wifeIndex = wifeIndex;
    }
}
