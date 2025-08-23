package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.dtos.UserDto;
import com.ummbatin.service_management.models.AuthenticationRequest;
import com.ummbatin.service_management.models.AuthenticationResponse;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.models.Role;

import com.ummbatin.service_management.services.AuthenticationService;
import com.ummbatin.service_management.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLogin_Success() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("test@email.com", "password");

        // إنشاء UserDto بشكل صحيح
        UserDto userDto = UserDto.builder()
                .user_id(1L)
                .fullName("Test User")
                .email("test@email.com")
                .phone("0501234567")
                .createdAt(LocalDateTime.now())
                .properties(Collections.emptyList())
                .wives(Collections.emptyList())
                .children(Collections.emptyList())
                .build();

        AuthenticationResponse response = new AuthenticationResponse("token", userDto);

        when(authService.login(any(AuthenticationRequest.class))).thenReturn(response);

        // Act
        ResponseEntity<?> result = authController.login(request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        assertNotNull(result.getBody());
        assertTrue(result.getBody() instanceof AuthenticationResponse);

        AuthenticationResponse responseBody = (AuthenticationResponse) result.getBody();
        assertEquals("token", responseBody.getToken());
        assertEquals("Test User", responseBody.getUser().getFullName());
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("wrong@email.com", "wrong");

        when(authService.login(any(AuthenticationRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        ResponseEntity<?> result = authController.login(request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Invalid credentials", result.getBody());
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("password");

        UserDto userDto = UserDto.builder()
                .user_id(1L)
                .fullName("Test User")
                .email("test@email.com")
                .build();

        AuthenticationResponse response = new AuthenticationResponse("token", userDto);

        when(authService.register(any(User.class))).thenReturn(response);

        // Act
        AuthenticationResponse result = authController.registerUser(user);

        // Assert
        assertNotNull(result);
        assertEquals("token", result.getToken());
        assertEquals("test@email.com", result.getUser().getEmail());
    }
}