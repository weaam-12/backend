// FamilyRegistrationDto.java
package com.ummbatin.service_management.dtos;

import com.ummbatin.service_management.models.User;
import lombok.Data;
import java.util.List;

@Data
public class FamilyRegistrationDto {
    private User user;
    private List<WifeDto> wives;        // just names
    private List<ChildDto> children;// nested
}


@Data
class WifeRegistrationDto {
    private String name;
}

@Data
class ChildRegistrationDto {
    private String name;
    private String birthDate;
    private String motherName;
}