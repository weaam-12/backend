package com.ummbatin.service_management.models;

import jakarta.persistence.*;
import com.ummbatin.service_management.models.User;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String message;
    private String type;
    private String status;
    private LocalDateTime createdAt;
}
