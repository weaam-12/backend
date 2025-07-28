package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.dtos.EnrollmentRequestDTO;
import com.ummbatin.service_management.models.Child;
import com.ummbatin.service_management.models.Enrollment;
import com.ummbatin.service_management.models.Kindergarten;
import com.ummbatin.service_management.services.EnrollmentService;
import com.ummbatin.service_management.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;

    // Resident can view their child's enrollments
    @GetMapping("/child/{childId}")
    @PreAuthorize("hasAnyRole('RESIDENT', 'ADMIN')")
    public List<Enrollment> getChildEnrollments(@PathVariable Long childId) {
        return enrollmentService.getChildEnrollments(childId);
    }



    // Resident can cancel their child's enrollment
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESIDENT', 'ADMIN')")
    public void cancelEnrollment(@PathVariable Long id) {
        enrollmentService.cancelEnrollment(id);
    }

    // Resident can update their child's enrollment status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RESIDENT', 'ADMIN')")
    public Enrollment updateEnrollmentStatus(@PathVariable Long id, @RequestBody String newStatus) {
        return enrollmentService.updateEnrollmentStatus(id, newStatus);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RESIDENT', 'ADMIN')")
    public ResponseEntity<?> enrollChild(@RequestBody EnrollmentRequestDTO enrollmentDTO) {
        try {
            Enrollment enrollment = new Enrollment();

            // تعيين الطفل
            Child child = new Child();
            child.setChildId(enrollmentDTO.getChildId().intValue());
            enrollment.setChild(child);

            // تعيين الحضانة
            Kindergarten kindergarten = new Kindergarten();
            kindergarten.setKindergartenId(enrollmentDTO.getKindergartenId());
            enrollment.setKindergarten(kindergarten);

            // تعيين الحقول الإضافية
            enrollment.setStatus(enrollmentDTO.getStatus() != null ? enrollmentDTO.getStatus() : "PENDING");
            enrollment.setPaymentIntentId(enrollmentDTO.getPaymentIntentId());

            // حفظ التسجيل
            Enrollment savedEnrollment = enrollmentService.enrollChild(enrollment);

            return ResponseEntity.ok(savedEnrollment);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error creating enrollment: " + e.getMessage()));
        }
    }
}