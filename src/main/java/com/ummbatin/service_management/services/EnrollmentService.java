package com.ummbatin.service_management.services;

import com.ummbatin.service_management.models.Enrollment;
import com.ummbatin.service_management.models.Kindergarten;
import com.ummbatin.service_management.repositories.EnrollmentRepository;
import com.ummbatin.service_management.repositories.KindergartenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final KindergartenRepository kindergartenRepository;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository, KindergartenRepository kindergartenRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.kindergartenRepository = kindergartenRepository;
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public List<Enrollment> getChildEnrollments(Long childId) {
        return enrollmentRepository.findByChild_ChildId(childId.intValue()); // If childId is Long
    }


    public Enrollment enrollChild(Enrollment enrollment) {
        Kindergarten kindergarten = enrollment.getKindergarten();

        // Check if the kindergarten has reached its capacity
        List<Enrollment> currentEnrollments = enrollmentRepository.findByKindergarten_KindergartenId(kindergarten.getKindergartenId());
        if (currentEnrollments.size() >= kindergarten.getCapacity()) {
            enrollment.setStatus("WAITLISTED");
        } else {
            enrollment.setStatus("ENROLLED");
        }

        return enrollmentRepository.save(enrollment);
    }

    public void cancelEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }

    public Enrollment updateEnrollmentStatus(Long enrollmentId, String newStatus) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
        enrollment.setStatus(newStatus);
        return enrollmentRepository.save(enrollment);
    }
}
