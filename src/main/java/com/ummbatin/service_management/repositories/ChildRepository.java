package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Integer> {
    List<Child> findByUser_UserId(Long userId);
    Optional<List<Child>> findByUserId(Long userId);
    Optional<List<Child>> findByWifeId(Long wifeId);


}