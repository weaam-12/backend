package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {  // Long matches User's id
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAllWithProperties();
}