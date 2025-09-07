package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Integer> {
    @Query("SELECT c FROM Child c WHERE c.user.userId = :userId")
    List<Child> findByUserId(@Param("userId") Long userId);

    // أو باستخدام صيغة JPA المدمجة
    List<Child> findByUser_UserId(Long userId);

    @Query("SELECT c FROM Child c WHERE c.wife.id = :wifeId")
    List<Child> findByWifeId(@Param("wifeId") Long wifeId);
}
