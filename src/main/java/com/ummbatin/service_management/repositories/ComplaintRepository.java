package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {
    List<Complaint> findByUserId(Long userId);
    List<Complaint> findAllByOrderByDateDesc();
}