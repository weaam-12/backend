package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.PublicService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicServiceRepository extends JpaRepository<PublicService, Long> {
}