package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.Enrollment;
import com.ummbatin.service_management.services.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Resident can enroll their child
    @PostMapping
    @PreAuthorize("hasAnyRole('RESIDENT', 'ADMIN')")
    public Enrollment enrollChild(@RequestBody Enrollment enrollment) {
        return enrollmentService.enrollChild(enrollment);
    }

    // Resident can cancel their child's enrollment
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESIDENT', 'ADMIN')")
    public void cancelEnrollment(@PathVariable Long id) {
        enrollmentService.cancelEnrollment(id);
    }
}
