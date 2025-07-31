package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.userId = 4 OR n.user IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findAdminNotifications();
}