package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.Notification;
import com.ummbatin.service_management.services.NotificationService;
import com.ummbatin.service_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> myNotifications(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            return ResponseEntity.ok(notificationService.getForUser(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching notifications: " + e.getMessage());
        }
    }

    @GetMapping("/admin")
    public List<Notification> adminNotifications() {
        return notificationService.getAdminNotifications();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // يجب تعديل هذه الدالة حسب طريقة تخزين معرف المستخدم في الـ token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();
    }
}