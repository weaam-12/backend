package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}