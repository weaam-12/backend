package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("SELECT a FROM Announcement a WHERE a.active = true AND (a.expiresAt IS NULL OR a.expiresAt > :now) ORDER BY a.priority DESC, a.createdAt DESC")
    List<Announcement> findActiveAnnouncements(LocalDateTime now);
}