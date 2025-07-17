package com.ummbatin.service_management.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.ummbatin.service_management.dtos.*;
import com.ummbatin.service_management.services.PaymentService;
import com.ummbatin.service_management.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments") // أضف /api هنا
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<PaymentDto>> getAllPayments(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long userId) {
        try {
            List<PaymentDto> payments = paymentService.getAllPayments(month, year, userId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDto>> getUserPayments(@PathVariable Long userId) {
        try {
            List<PaymentDto> payments = paymentService.getUserPayments(userId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<PaymentDto>> getRecentPayments(@PathVariable Long userId) {
        try {
            List<PaymentDto> payments = paymentService.getRecentPayments(userId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/generate-water")
    public ResponseEntity<ApiResponse> generateWaterBills(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam double rate,
            @RequestParam(required = false) Long userId) {
        try {
            ApiResponse response = paymentService.generateWaterBills(month, year, rate, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to generate water bills: " + e.getMessage()));
        }
    }

    @PostMapping("/generate-arnona")
    public ResponseEntity<ApiResponse> generateArnonaBills(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(required = false) Long userId) {
        try {
            ApiResponse response = paymentService.generateArnonaBills(month, year, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to generate arnona bills: " + e.getMessage()));
        }
    }

    @PutMapping("/update-fee")
    public ResponseEntity<ApiResponse> updatePaymentFee(
            @RequestParam Long userId,
            @RequestParam String paymentType,
            @RequestParam double newAmount) {
        try {
            ApiResponse response = paymentService.updatePaymentFee(userId, paymentType, newAmount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to update payment fee: " + e.getMessage()));
        }
    }

    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<ApiResponse> updatePaymentStatus(
            @PathVariable Integer paymentId,
            @RequestBody UpdatePaymentStatusRequest request) {
        try {
            ApiResponse response = paymentService.updatePaymentStatus(paymentId, request.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to update payment status: " + e.getMessage()));
        }
    }

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentResponse response = paymentService.processPayment(paymentRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Payment processing failed: " + e.getMessage()));
        }
    }
}