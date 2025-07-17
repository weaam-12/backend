package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Integer> {
    List<Child> findByUserId(Long userId);
    List<Child> findByWifeId(Long wifeId);
}