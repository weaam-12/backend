package com.ummbatin.service_management.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.ummbatin.service_management.dtos.*;
import com.ummbatin.service_management.models.Payment;
import com.ummbatin.service_management.models.Property;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.models.WaterReading;
import com.ummbatin.service_management.repositories.PaymentRepository;
import com.ummbatin.service_management.repositories.PropertyRepository;
import com.ummbatin.service_management.repositories.UserRepository;
import com.ummbatin.service_management.repositories.WaterReadingRepository;
import com.ummbatin.service_management.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    public PaymentIntent createPaymentIntent(Long amountInCents) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .addPaymentMethodType("card")
                .build();
        return PaymentIntent.create(params);
    }
    public Payment createPayment(Long userId, Double amount, String type, String transactionId) {
        // 1. التحقق من وجود فاتورة لنفس المستخدم ونفس النوع في الشهر الحالي
        LocalDate now = LocalDate.now();
        LocalDate firstOfMonth = now.withDayOfMonth(1);
        LocalDate lastOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<Payment> existingPayments = paymentRepository.findByUser_UserIdAndTypeAndDateBetween(
                userId,
                type,
                firstOfMonth,
                lastOfMonth
        );

        if (!existingPayments.isEmpty()) {
            throw new RuntimeException("يوجد فاتورة " + type + " لهذا المستخدم في الشهر الحالي بالفعل");
        }

        // 2. إنشاء الفاتورة الجديدة
        Payment payment = new Payment();
        payment.setUser(userRepository.findById(userId).orElseThrow());
        payment.setProperty(propertyRepository.findByUser_UserId(userId).get(0)); // أول عقار للمستخدم
        payment.setAmount(amount);
        payment.setType(type);
        payment.setDate(now);
        payment.setStatus("PENDING");
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByType(String type) {
        return paymentRepository.findByType(type);
    }

    public List<Payment> getUserPaymentsByType(Long userId, String type) {
        return paymentRepository.findByUser_UserIdAndType(userId, type);
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
            List<User> users = userId != null
                    ? List.of(userRepository.findById(userId).orElseThrow())
                    : userRepository.findAll();

            for (User user : users) {
                for (Property property : user.getProperties()) {
                    double amount = calculateWaterAmount(property, rate);

                    // ✅ تجنب التكرار: لو فيه فاتورة مياه في نفس الشهر متضيفهاش
                    boolean exists = paymentRepository.existsByUserAndTypeAndDateBetween(
                            user, "WATER",
                            LocalDate.of(year, month, 1),
                            LocalDate.of(year, month, 1).withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth())
                    );

                    if (exists) continue;

                    Payment payment = new Payment();
                    payment.setUser(user);
                    payment.setProperty(property);
                    payment.setAmount(amount);
                    payment.setType("WATER");
                    payment.setStatus("PENDING");
                    payment.setDate(LocalDate.of(year, month, 1));
                    payment.setServiceId(2L); // مثلاً: 2 = خدمة المياه
                    payment.setPaymentDate(LocalDateTime.now());

                    paymentRepository.save(payment);
                }
            }

            return new ApiResponse(true, "تم توليد فواتير المياه بنجاح");
        }

    private double calculateWaterAmount(Property property, double rate) {
        if (property == null || property.getArea() == null) return 0.0;
        return property.getArea().doubleValue() * rate;
    }

    public ApiResponse generateArnonaBills(int month, int year, Long userId) {
        List<User> users = userId != null ? List.of(userRepository.findById(userId).orElseThrow()) : userRepository.findAll();

        for (User user : users) {
            for (Property property : user.getProperties()) {
                double amount = property.getArea().doubleValue() * 3.0; // حساب الأرنونا بناءً على المساحة أو عدد الوحدات

                Payment payment = new Payment();
                payment.setUser(user);
                payment.setProperty(property);
                payment.setAmount(amount);
                payment.setType("ARNONA");
                payment.setStatus("PENDING");
                payment.setDate(LocalDate.of(year, month, 1));
                payment.setServiceId(1L); // مثلاً: خدمة الأرنونا
                payment.setPaymentDate(LocalDateTime.now());

                paymentRepository.save(payment); // ✅ هنا الحفظ
            }
        }

        return new ApiResponse(true, "تم توليد فواتير الأرنونا بنجاح");
    }

    public ApiResponse updatePaymentFee(Long userId, String paymentType, double newAmount) {
        // تنفيذ المنطق المطلوب لتحديث رسوم الدفع
        return new ApiResponse(true, "Payment fee updated successfully");
    }

    public ApiResponse updatePaymentStatus(Integer paymentId, String status) {
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
            dto.setDate(payment.getDate());
            dto.setPaymentDate(payment.getPaymentDate() != null ?
                    payment.getPaymentDate().toLocalDate() : null);
            return dto;
        }).collect(Collectors.toList());
    }

    public PaymentResponse processPayment(PaymentRequest paymentRequest) throws StripeException {
        PaymentIntent paymentIntent = createPaymentIntent(paymentRequest.getAmount());

        Payment payment = new Payment();
        payment.setUser(new User());
        payment.getUser().setUserId(paymentRequest.getUserId());
        payment.setAmount(paymentRequest.getAmount().doubleValue() / 100);
        payment.setType(paymentRequest.getPaymentType());
        payment.setStatus("PENDING");
        payment.setTransactionId(paymentIntent.getId());
        payment.setDate(LocalDate.now());
        paymentRepository.save(payment);

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(paymentIntent.getId());
        response.setClientSecret(paymentIntent.getClientSecret());
        response.setAmount(paymentRequest.getAmount());
        response.setCurrency(paymentRequest.getCurrency());
        response.setDescription(paymentRequest.getDescription());
        response.setStatus("requires_payment_method");

        return response;
    }
    public ApiResponse generateCustomWaterPayments(List<CustomWaterPaymentRequest> requests) {
        try {
            LocalDateTime currentDateTime = LocalDateTime.now();
            List<Payment> generatedPayments = new ArrayList<>();

            for (CustomWaterPaymentRequest request : requests) {
                if (request.getAmount() == null || request.getAmount() <= 0) continue;

                User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

                Property property = propertyRepository.findById(Math.toIntExact(request.getPropertyId()))
                        .orElseThrow(() -> new RuntimeException("Property not found with id: " + request.getPropertyId()));

                // Create payment
                Payment payment = new Payment();
                payment.setUser(user);
                payment.setProperty(property);
                payment.setServiceId(1L); // Assuming 1 is for water
                payment.setAmount(request.getAmount());
                payment.setDate(currentDateTime.toLocalDate());
                payment.setStatus("PENDING");
                payment.setType("WATER");

                // Save water reading if provided
                if (request.getCurrentReading() != null && request.getCurrentReading() > 0) {
                    WaterReading reading = new WaterReading();
                    reading.setProperty(property);
                    reading.setAmount(request.getCurrentReading());
                    reading.setDate(currentDateTime);
                    reading.setApproved(true);
                    reading.setManual(request.getManual() != null ? request.getManual() : false);
                    waterReadingRepository.save(reading);
                }

                generatedPayments.add(paymentRepository.save(payment));
            }

            return new ApiResponse(true, "Successfully generated " + generatedPayments.size() + " water payments");
        } catch (Exception e) {
            throw new RuntimeException("Error generating custom water payments: " + e.getMessage(), e);
        }
    }

    @Autowired
    private WaterReadingRepository waterReadingRepository;

    public List<UserWithPaymentsDto> getUsersWithPayments(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            UserWithPaymentsDto dto = new UserWithPaymentsDto();
            dto.setUserId(user.getUserId());
            dto.setUserName(user.getFullName());

            List<Property> userProperties = propertyRepository.findByUser_UserId(user.getUserId());
            if (!userProperties.isEmpty()) {
                Property property = userProperties.get(0);
                dto.setPropertyId(property.getPropertyId());
                dto.setPropertyAddress(property.getAddress());

                // Get latest water reading
                List<WaterReading> readings = waterReadingRepository.findByProperty_PropertyIdOrderByDateDesc(property.getPropertyId());
                if (!readings.isEmpty()) {
                    WaterReading latestReading = readings.get(0);
                    dto.setLastWaterReading(latestReading.getAmount());
                    dto.setManual(latestReading.getManual());
                }
            }

            List<Payment> payments = paymentRepository.findByUserAndDateBetween(user, startDate, endDate);
            payments.forEach(payment -> {
                if ("WATER".equals(payment.getType())) {
                    dto.setWaterAmount(payment.getAmount());
                    dto.setWaterPaymentId(payment.getPaymentId());
                    dto.setWaterStatus(payment.getStatus());
                } else if ("ARNONA".equals(payment.getType())) {
                    dto.setArnonaAmount(payment.getAmount());
                    dto.setArnonaPaymentId(payment.getPaymentId());
                    dto.setArnonaStatus(payment.getStatus());
                }
            });

            return dto;
        }).collect(Collectors.toList());
    }

}