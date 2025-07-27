package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.AuthenticationRequest;
import com.ummbatin.service_management.models.AuthenticationResponse;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.services.AuthenticationService;
import com.ummbatin.service_management.dtos.FamilyRegistrationDto;
import com.ummbatin.service_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authService;

    @PostMapping("/register-family")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerFamily(@RequestBody Map<String, Object> request) {
        System.out.println("Received data: " + request); // للتصحيح
        try {
            User savedUser = userService.registerFamily(request);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public AuthenticationResponse registerUser(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
