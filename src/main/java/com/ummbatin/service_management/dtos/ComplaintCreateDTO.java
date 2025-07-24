package com.ummbatin.service_management.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplaintCreateDTO {
    @NotNull
    private Long   userId;
    @NotBlank private String type;
    @NotBlank
    private String description;
    @NotBlank private String location;



}