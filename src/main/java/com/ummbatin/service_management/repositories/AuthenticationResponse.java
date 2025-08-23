package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.dtos.UserDto;

public class AuthenticationResponse {
    private String token;
    private UserDto user;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
