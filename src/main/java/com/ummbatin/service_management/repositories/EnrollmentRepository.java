package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByChild_ChildId(Integer childId); // for child enrollments

    List<Enrollment> findByKindergarten_KindergartenId(Integer kindergartenId); // for kindergarten capacity check
}
