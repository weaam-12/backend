// FamilyRegistrationDto.java
package com.ummbatin.service_management.dtos;

import com.ummbatin.service_management.models.User;
import lombok.Data;
import java.util.List;

@Data
public class FamilyRegistrationDto {
    private UserDto user;
    private List<WifeRegistrationDto> wives;
    private List<ChildRegistrationDto> children;
}

@Data
class WifeRegistrationDto {
    private String name;
}

@Data
class ChildRegistrationDto {
    private String name;
    private String birthDate;
    private int wifeIndex; // تغيير من motherName إلى wifeIndex
}