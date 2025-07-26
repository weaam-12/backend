// FamilyRegistrationDto.java
package com.ummbatin.service_management.dtos;

import com.ummbatin.service_management.models.User;
import lombok.Data;
import java.util.List;

@Data
public class FamilyRegistrationDto {
    private User user;
    private List<String> wives;        // just names
    private List<ChildDto> children;

    // nested
}