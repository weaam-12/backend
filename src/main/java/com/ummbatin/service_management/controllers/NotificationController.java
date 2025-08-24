package com.ummbatin.service_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ummbatin.service_management.dtos.NotificationCreateRequest;
import com.ummbatin.service_management.dtos.NotificationDTO;
import com.ummbatin.service_management.models.Notification;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.repositories.UserRepository;
import com.ummbatin.service_management.services.NotificationService;
import com.ummbatin.service_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;


    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationDTO>> myNotifications(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Notification> notifications = notificationService.getForUser(user.getUserId());

            List<NotificationDTO> dtos = notifications.stream()
                    .map(n -> new NotificationDTO(
                            n.getNotificationId(),
                            n.getMessage(),
                            n.getType(),
                            n.getStatus(),
                            n.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody NotificationCreateRequest req) {
        try {
            User user = userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            notificationService.createUserNotification(
                    user.getUserId(),
                    req.getMessage(),
                    req.getType());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace(); // ✅ أضف هذا
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin")
    public List<NotificationDTO> adminNotifications() {
        return notificationService.getAdminNotifications()
                .stream()
                .map(n -> new NotificationDTO(
                        n.getNotificationId(),
                        n.getMessage(),
                        n.getType(),
                        n.getStatus(),
                        n.getCreatedAt()))
                .collect(Collectors.toList());
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