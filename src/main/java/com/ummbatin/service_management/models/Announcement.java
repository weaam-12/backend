package com.ummbatin.service_management.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean active = true;

    private int priority = 0;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime expiresAt;
}