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
    public ApiResponse generateWaterBillForProperty(int month, int year, double amount, Long userId, Long propertyId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Property prop = propertyRepository.findById(Math.toIntExact(propertyId)).orElseThrow(() -> new RuntimeException("Property not found"));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        boolean exists = paymentRepository.existsByUserAndPropertyAndTypeAndDateBetween(
                user, prop, "WATER", start, end);
        if (exists) {
            return new ApiResponse(false, "فاتورة موجودة مسبقًا");
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setProperty(prop);
        payment.setAmount(amount);
        payment.setType("WATER");
        payment.setStatus("PENDING");
        payment.setDate(start);
        payment.setServiceId(2L); // خدمة المياه
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);
        return new ApiResponse(true, "تم توليد فاتورة مياه بنجاح");
    }
    public ApiResponse generateWaterBill(double amount, Long userId, Long propertyId, int month, int year) {
        User user = userRepository.findById(userId).orElseThrow();
        Property property = propertyRepository.findById(Math.toIntExact(propertyId)).orElseThrow();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        boolean exists = paymentRepository.existsByUserAndPropertyAndTypeAndDateBetween(
                user, property, "WATER", start, end);
        if (exists) {
            return new ApiResponse(false, "فاتورة المياه موجودة مسبقًا لهذا العقار في هذا الشهر");
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setProperty(property);
        payment.setAmount(amount);
        payment.setType("WATER");
        payment.setStatus("PENDING");
        payment.setDate(start);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setServiceId(2L); // خدمة المياه

        paymentRepository.save(payment);

        return new ApiResponse(true, "تم توليد فاتورة مياه للعقار بنجاح");
    }

    private PaymentDto toDto(Payment p) {
        return new PaymentDto(
                p.getPaymentId().longValue(),
                p.getUser().getUserId(),
                p.getUser().getFullName(),
                p.getType(),
                p.getAmount(),
                p.getStatus(),
                p.getDate(),
                p.getProperty().getAddress(),
                p.getProperty().getArea().doubleValue(),
                p.getProperty().getNumberOfUnits()
        );
    }

    public List<PaymentDto> getAllPayments(Integer month, Integer year, Long userId) {
        List<Payment> list = (userId == null) ? paymentRepository.findAll()
                : paymentRepository.findByUser_UserId(userId);
        return list.stream()
                .filter(p -> (month == null || p.getDate().getMonthValue() == month)
                        && (year  == null || p.getDate().getYear() == year))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<PaymentDto> getUserPayments(Long userId) {
        return paymentRepository.findByUser_UserId(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<PaymentDto> getRecentPayments(Long userId) {
        LocalDateTime cutoff = LocalDateTime.now().minusMonths(1);
        return paymentRepository.findByUser_UserIdAndPaymentDateAfter(userId, cutoff)
                .stream().map(this::toDto).collect(Collectors.toList());
    }
    private double calculateWaterAmount(Property property, double rate) {
        if (property == null || property.getArea() == null) return 0.0;
        return property.getArea().doubleValue() * rate;
    }

    public ApiResponse generateArnonaBills(int month, int year, Long userId) {
        List<User> users = userId != null ? List.of(userRepository.findById(userId).orElseThrow()) : userRepository.findAll();

        for (User user : users) {
            for (Property property : user.getProperties()) {
                double amount = property.getArea().doubleValue() * 50 * property.getNumberOfUnits(); // حساب الأرنونا بناءً على المساحة أو عدد الوحدات

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
        return new ApiResponse(false, "Payment bbnnot found");
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


    public PaymentResponse processPayment(PaymentRequest paymentRequest) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        // 1. الحصول على المستخدم والعقار
        User user = userRepository.findById(paymentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Property property = propertyRepository.findByUser_UserId(user.getUserId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User has no properties"));

        // 2. إنشاء PaymentIntent مع Stripe
        PaymentIntent paymentIntent = createPaymentIntent(paymentRequest.getAmount());

        // 3. إنشاء كائن Payment وتعيين جميع الحقول المطلوبة
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setProperty(property); // تأكد من تعيين العقار
        payment.setAmount(paymentRequest.getAmount() / 100.0); // تحويل المبلغ من سنتات
        payment.setType(paymentRequest.getPaymentType());
        payment.setStatus("PENDING");
        payment.setTransactionId(paymentIntent.getId());
        payment.setDate(LocalDate.now());
        payment.setServiceId(1L); // تعيين serviceId افتراضي إذا كان غير متوفر

        paymentRepository.save(payment); // الحفظ في قاعدة البيانات

        // 4. إعداد الاستجابة
        PaymentResponse response = new PaymentResponse();
        response.setClientSecret(paymentIntent.getClientSecret());
        return response;
    }
}