package com.ummbatin.service_management.dtos;

import lombok.Data;
@Data
public class ChildDto {
    private String name;
    private String birthDate; // yyyy-MM-dd
    private int wifeIndex;    // index in wives list
}

