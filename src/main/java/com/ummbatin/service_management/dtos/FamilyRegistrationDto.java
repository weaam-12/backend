// FamilyRegistrationDto.java
package com.ummbatin.service_management.dtos;

import com.ummbatin.service_management.models.User;
import lombok.Data;
import java.util.List;

@Data
public class FamilyRegistrationDto {
    private UserDto user;
    private Long userId;
    private List<WifeRegistrationDto> wives;
    private List<ChildRegistrationDto> children;
}


