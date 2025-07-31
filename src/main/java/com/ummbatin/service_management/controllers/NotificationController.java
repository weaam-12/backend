package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.Notification;
import com.ummbatin.service_management.services.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/me")
    public List<Notification> myNotifications(Authentication authentication) {
        // يجب أن تقوم بتنفيذ منطق جلب معرف المستخدم من authentication
        Long userId = getUserIdFromAuthentication(authentication);
        return notificationService.getForUser(userId);
    }

    @GetMapping("/admin")
    public List<Notification> adminNotifications() {
        return notificationService.getAdminNotifications();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // تنفيذ منطق جلب معرف المستخدم من authentication
        // هذا مثال - يجب تعديله حسب تطبيقك
        return Long.parseLong(authentication.getName());
    }
}