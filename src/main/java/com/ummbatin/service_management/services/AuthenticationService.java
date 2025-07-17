package com.ummbatin.service_management.services;

import com.ummbatin.service_management.dtos.UserDto;
import com.ummbatin.service_management.models.AuthenticationRequest;
import com.ummbatin.service_management.models.AuthenticationResponse;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.repositories.UserRepository;
import com.ummbatin.service_management.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // BCryptPasswordEncoder bean

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse register(User user) {
        // Validate role is non-null and valid here

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        //  encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // Generate JWT with email and role info
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getRoleName());

        //  add timestamp or traceId to AuthenticationResponse
        UserDto userDto = new UserDto(user);
        return new AuthenticationResponse(token, userDto);
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getRoleName());
        UserDto userDto = new UserDto(user);
        return new AuthenticationResponse(token, userDto);
    }



}
