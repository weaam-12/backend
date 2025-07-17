package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Kindergarten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KindergartenRepository extends JpaRepository<Kindergarten, Integer> {
    // You can add custom queries if needed
}
