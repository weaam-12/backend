package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.Notification;
import com.ummbatin.service_management.services.NotificationService;
import com.ummbatin.service_management.services.UserService;
import com.ummbatin.service_management.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService,
                                  UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }
    @GetMapping("/me")

    public List<Notification> myNotifications(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationService.getForUser(user.getUserId());
    }
}
