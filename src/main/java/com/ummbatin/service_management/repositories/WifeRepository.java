package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Wife;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WifeRepository extends JpaRepository<Wife, Long> {
    List<Wife> findByUser_UserId(Long userId);
}