package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId")
    List<Notification> findByUser(@Param("userId") Long userId);

    List<Notification> findByUser_UserId(Long userId);
}
