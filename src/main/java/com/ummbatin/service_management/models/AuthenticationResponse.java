package com.ummbatin.service_management.models;

import com.ummbatin.service_management.dtos.UserDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponse {
    private String token;
    private UserDto user;
}