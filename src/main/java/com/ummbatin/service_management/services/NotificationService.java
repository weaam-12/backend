package com.ummbatin.service_management.services;

import com.ummbatin.service_management.models.Notification;
import com.ummbatin.service_management.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Fetch all notifications for the given user ID.
     */
    public List<Notification> getForUser(Long userId) {
        return notificationRepository.findByUser_UserId(userId);
    }
}
