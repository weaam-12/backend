package com.ummbatin.service_management.controllers;

import com.stripe.model.PaymentIntent;
import com.ummbatin.service_management.dtos.EnrollmentRequestDTO;
import com.ummbatin.service_management.exceptions.ResourceNotFoundException;
import com.ummbatin.service_management.models.Child;
import com.ummbatin.service_management.models.Enrollment;
import com.ummbatin.service_management.models.Kindergarten;
import com.ummbatin.service_management.models.Payment;
import com.ummbatin.service_management.services.ChildService;
import com.ummbatin.service_management.services.EnrollmentService;
import com.ummbatin.service_management.services.KindergartenService;
import com.ummbatin.service_management.services.PaymentService;
import com.ummbatin.service_management.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;
    private ChildService childService;
    private PaymentService paymentService;
    private KindergartenService kindergartenService;
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
            // 1. التحقق من وجود الطفل والحضانة
            Child child = childService.getChildById(enrollmentDTO.getChildId())
                    .orElseThrow(() -> new ResourceNotFoundException("Child not found"));

            Kindergarten kindergarten = kindergartenService.getKindergartenById(enrollmentDTO.getKindergartenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kindergarten not found"));

            // 2. معالجة الدفع مع Stripe
            PaymentIntent intent = PaymentIntent.retrieve(enrollmentDTO.getPaymentMethodId());
            if (!"succeeded".equals(intent.getStatus())) {
                throw new RuntimeException("Payment not completed");
            }

            // 3. إنشاء سجل الدفع
            Payment payment = paymentService.createPayment(
                    child.getUser().getUserId(),
                    250.9, // المبلغ الثابت
                    "KINDERGARTEN",
                    intent.getId()
            );

            // 4. إنشاء التسجيل
            Enrollment enrollment = new Enrollment();
            enrollment.setChild(child);
            enrollment.setKindergarten(kindergarten);
            enrollment.setStatus("ACTIVE");
            enrollment.setPayment(payment);

            Enrollment savedEnrollment = enrollmentService.enrollChild(enrollment);

            // 5. إرجاع الاستجابة
            Map<String, Object> response = new HashMap<>();
            response.put("enrollment", savedEnrollment);
            response.put("paymentId", payment.getPaymentId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error creating enrollment: " + e.getMessage()));
        }
    }
}