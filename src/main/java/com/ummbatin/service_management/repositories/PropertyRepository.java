package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {
    List<Property> findByUser_UserId(Long userId);
}