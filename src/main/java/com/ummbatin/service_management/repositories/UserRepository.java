package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {  // Long matches User's id
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.properties WHERE u.properties IS NOT EMPTY")
    List<User> findAllWithProperties();
    @Query("SELECT DISTINCT u FROM User u")
    Page<User> findAllUsers(Pageable pageable);

    Page<User> findAll(Pageable pageable);



}