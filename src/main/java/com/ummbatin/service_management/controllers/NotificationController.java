package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.Notification;
import com.ummbatin.service_management.models.User;
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
    public ResponseEntity<List<Notification>> myNotifications(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<Notification> notifications = notificationService.getForUser(userId);

            // تأكد من أن القائمة ليست null
            if (notifications == null) {
                notifications = Collections.emptyList();
            }

            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/admin")
    public List<Notification> adminNotifications() {
        return notificationService.getAdminNotifications();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // استخدم email للبحث عن المستخدم
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getUserId(); // تأكد أن هذه القيمة ليست null
    }
}