package com.ummbatin.service_management.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.ummbatin.service_management.dtos.PaymentDto;
import com.ummbatin.service_management.dtos.PaymentRequest;
import com.ummbatin.service_management.dtos.PaymentResponse;
import com.ummbatin.service_management.models.Payment;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.repositories.PaymentRepository;
import com.ummbatin.service_management.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    // الدوال الحالية
    public PaymentIntent createPaymentIntent(Long amountInCents) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .addPaymentMethodType("card")
                .build();
        return PaymentIntent.create(params);
    }

    public void markPaymentAsCompleted(Long userId, String serviceId, String paymentIntentId, String receiptEmail) {
        Optional<Payment> optionalPayment = paymentRepository.findByUser_UserIdAndServiceIdAndTransactionId(
                userId,
                Long.parseLong(serviceId),
                paymentIntentId
        );
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setStatus("COMPLETED");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setReceiptEmail(receiptEmail);
            paymentRepository.save(payment);
        }
    }

    public ApiResponse generateWaterBills(int month, int year, double rate, Long userId) {
        // تنفيذ المنطق المطلوب لتوليد فواتير المياه
        return new ApiResponse(true, "Water bills generated successfully");
    }

    public ApiResponse generateArnonaBills(int month, int year, Long userId) {
        // تنفيذ المنطق المطلوب لتوليد فواتير الأرنونا
        return new ApiResponse(true, "Arnona bills generated successfully");
    }

    public ApiResponse updatePaymentFee(Long userId, String paymentType, double newAmount) {
        // تنفيذ المنطق المطلوب لتحديث رسوم الدفع
        return new ApiResponse(true, "Payment fee updated successfully");
    }

    public ApiResponse updatePaymentStatus(Integer paymentId, String status) {
        // تنفيذ المنطق المطلوب لتحديث حالة الدفع
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        if (payment.isPresent()) {
            Payment p = payment.get();
            p.setStatus(status);
            paymentRepository.save(p);
            return new ApiResponse(true, "Payment status updated successfully");
        }
        return new ApiResponse(false, "Payment not found");
    }

    private PaymentDto convertToDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getPaymentId().longValue());
        dto.setUserId(payment.getUser().getUserId());
        dto.setPaymentType(payment.getType());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        return dto;
    }

    public List<PaymentDto> getUserPayments(Long userId) {
        List<Payment> payments = paymentRepository.findByUser_UserId(userId);
        return payments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<PaymentDto> getRecentPayments(Long userId) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(1);
        List<Payment> payments = paymentRepository.findByUser_UserIdAndPaymentDateAfter(userId, cutoffDate);
        return payments.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    public List<PaymentDto> getAllPayments(Integer month, Integer year, Long userId) {
        List<Payment> payments;
        if (userId != null) {
            payments = paymentRepository.findByUser_UserId(userId);
        } else {
            payments = paymentRepository.findAll();
        }

        return payments.stream().map(payment -> {
            PaymentDto dto = new PaymentDto();
            dto.setPaymentId(payment.getPaymentId().longValue());
            dto.setUserId(payment.getUser().getUserId());
            dto.setUserName(payment.getUser().getFullName());
            dto.setPaymentType(payment.getType());
            dto.setAmount(payment.getAmount());
            dto.setStatus(payment.getStatus());
            dto.setDate(payment.getDate()); // استخدام date كـ dueDate
            dto.setPaymentDate(payment.getPaymentDate() != null ?
                    payment.getPaymentDate().toLocalDate() : null);
            return dto;
        }).collect(Collectors.toList());
    }
    public PaymentResponse processPayment(PaymentRequest paymentRequest) throws StripeException {
        // Create payment intent with Stripe
        PaymentIntent paymentIntent = createPaymentIntent(paymentRequest.getAmount());

        // Save payment record in database
        Payment payment = new Payment();
        payment.setUser(new User()); // You'll need to set the actual user from repository
        payment.getUser().setUserId(paymentRequest.getUserId());
        payment.setAmount(paymentRequest.getAmount().doubleValue() / 100); // Convert cents to dollars
        payment.setType(paymentRequest.getPaymentType());
        payment.setStatus("PENDING");
        payment.setTransactionId(paymentIntent.getId());
        payment.setDate(LocalDate.now());
        paymentRepository.save(payment);

        // Create and return response
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(paymentIntent.getId());
        response.setClientSecret(paymentIntent.getClientSecret());
        response.setAmount(paymentRequest.getAmount());
        response.setCurrency(paymentRequest.getCurrency());
        response.setDescription(paymentRequest.getDescription());
        response.setStatus("requires_payment_method"); // Initial Stripe status

        return response;
    }

}