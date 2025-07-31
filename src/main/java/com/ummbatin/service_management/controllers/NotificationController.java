package com.ummbatin.service_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ummbatin.service_management.models.Notification;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.services.NotificationService;
import com.ummbatin.service_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    @Autowired
    private UserService userService;
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/me")
    public ResponseEntity<String> myNotifications(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\":\"Not authenticated\"}");
            }

            Long userId = getUserIdFromAuthentication(authentication);
            List<Notification> notifications = notificationService.getForUser(userId);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(notifications != null ? notifications : Collections.emptyList());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Internal server error\"}");
        }
    }

    @GetMapping("/admin")
    public List<Notification> adminNotifications() {
        return notificationService.getAdminNotifications();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Authentication not available");
        }

        // Handle case where principal might be the email directly
        if (authentication.getPrincipal() instanceof String) {
            String email = (String) authentication.getPrincipal();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return user.getUserId();
        }

        // Handle UserDetails case
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return user.getUserId();
        }

        throw new RuntimeException("Unsupported principal type");
    }
}