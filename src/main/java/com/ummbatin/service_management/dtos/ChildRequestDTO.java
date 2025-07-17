package com.ummbatin.service_management.dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ChildRequestDTO {
    private String name;
    private LocalDate birthDate;
    private Long wifeId;
    private Long userId;
    private Integer kindergartenId;
}