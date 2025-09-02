package com.ummbatin.service_management.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.ummbatin.service_management.dtos.*;
import com.ummbatin.service_management.models.*;
import com.ummbatin.service_management.repositories.*;
import com.ummbatin.service_management.services.EnrollmentService;
import com.ummbatin.service_management.services.PaymentService;

import com.ummbatin.service_management.services.StripeService;
import com.ummbatin.service_management.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ummbatin.service_management.repositories.ChildRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments") // أضف /api هنا
public class PaymentController {

    private final PaymentService paymentService;
    @Autowired
    private UserRepository userRepository;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private KindergartenRepository kindergartenRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private StripeService stripeService;


    @Autowired
    private EnrollmentService enrollmentService;
    @Value("${stripe.api.key}")
    private String apiKey;
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


    @PostMapping("/simulate-payment")
    public ResponseEntity<?> simulatePayment(
            @RequestParam Long userId,
            @RequestParam Double amount,
            @RequestParam String type,
            @RequestParam Long propertyId) {

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Property property = propertyRepository.findById(Math.toIntExact(propertyId))
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            Payment payment = new Payment();
            payment.setUser(user);
            payment.setAmount(amount);
            payment.setType(type.toUpperCase());
            payment.setStatus("PAID");
            payment.setTransactionId("sim-" + System.currentTimeMillis());
            payment.setPaymentDate(LocalDate.now().atStartOfDay());
            payment.setProperty(property);

            Payment saved = paymentRepository.save(payment);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "paymentId", saved.getPaymentId(),
                    "message", "החיוב בוצע בהצלחה (מדומה)"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/validate-and-record")
    public ResponseEntity<?> validateAndRecord(
            @RequestParam Long userId,
            @RequestParam Double amount,     // المبلغ بالدولار
            @RequestParam String paymentMethodId, // الـ ID اللي ترجعه Stripe.js
            @RequestParam String type        // مثلاً KINDERGARTEN
    ) {
        try {
            long amountCents = (long)(amount * 100);

            PaymentIntent intent = PaymentIntent.create(
                    PaymentIntentCreateParams.builder()
                            .setAmount(amountCents)
                            .setCurrency("ils")          // أو "usd"
                            .setPaymentMethod(paymentMethodId)
                            .setConfirm(true)            // تأكيد فوري
                            .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL) // ما نخصم لحالنا
                            .build()
            );

            // 3. لو التأكيد نجح
            if ("succeeded".equalsIgnoreCase(intent.getStatus())) {
                // سجّل الدفع في قاعدة البيانات
                Payment payment = paymentService.createPayment(
                        userId, amount, type, intent.getId()
                );
                payment.setStatus("PAID");
                paymentRepository.save(payment);

                return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "paymentId", payment.getPaymentId()
                ));
            } else {
                // البطاقة غير صالحة
                return ResponseEntity.ok(Map.of(
                        "valid", false,
                        "error", "بطاقة غير صالحة"
                ));
            }

        } catch (StripeException ex) {
            return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "error", ex.getMessage()
            ));
        }
    }

    @PostMapping("/generate-water")
    public ResponseEntity<ApiResponse> generateWaterBills(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam double amount,
            @RequestParam Long userId,
            @RequestParam Long propertyId
    ) {
        try {
            ApiResponse response = paymentService.generateWaterBillForProperty(
                    month, year, amount, userId, propertyId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to generate water bill: " + e.getMessage()));
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
    @GetMapping("/current-month")
    public ResponseEntity<List<PaymentDto>> getCurrentMonthPayments() {
        try {
            int currentMonth = LocalDate.now().getMonthValue();
            int currentYear = LocalDate.now().getYear();
            List<PaymentDto> payments = paymentService.getAllPayments(currentMonth, currentYear, null);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/users-with-payments")
    public ResponseEntity<List<UserWithPaymentsDto>> getUsersWithPayments(
            @RequestParam int month,
            @RequestParam int year) {
        try {
            List<UserWithPaymentsDto> result = paymentService.getUsersWithPayments(month, year);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // إنشاء فاتورة مياه
    @PostMapping("/create-water")
    public ResponseEntity<Payment> createWaterPayment(
            @RequestParam Long userId,
            @RequestParam Double amount,
            @RequestParam(required = false) String transactionId) {
        Payment payment = paymentService.createPayment(userId, amount, "WATER", transactionId);
        return ResponseEntity.ok(payment);
    }

    // إنشاء فاتورة أرنونا
    @PostMapping("/create-arnona")
    public ResponseEntity<Payment> createArnonaPayment(
            @RequestParam Long userId,
            @RequestParam Double amount,
            @RequestParam(required = false) String transactionId) {
        Payment payment = paymentService.createPayment(userId, amount, "ARNONA", transactionId);
        return ResponseEntity.ok(payment);
    }

    // الحصول على جميع الفواتير حسب النوع
    @GetMapping("/by-type")
    public ResponseEntity<List<Payment>> getPaymentsByType(@RequestParam String type) {
        List<Payment> payments = paymentService.getPaymentsByType(type);
        return ResponseEntity.ok(payments);
    }

    // الحصول على فواتير مستخدم معين حسب النوع
    @GetMapping("/user/{userId}/by-type")
    public ResponseEntity<List<Payment>> getUserPaymentsByType(
            @PathVariable Long userId,
            @RequestParam String type) {
        List<Payment> payments = paymentService.getUserPaymentsByType(userId, type);
        return ResponseEntity.ok(payments);
    }
    @PostMapping("/generate-custom-water")
    public ResponseEntity<ApiResponse> generateCustomWaterPayments(
            @RequestBody List<CustomWaterPaymentRequest> requests) {
        try {
            ApiResponse response = paymentService.generateCustomWaterPayments(requests);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to generate custom water payments: " + e.getMessage()));
        }
    }



    @PostMapping("/create-kindergarten")
    public ResponseEntity<?> createKindergartenPayment(
            @RequestBody KindergartenPaymentRequest request) {
        try {
            // التحقق من وجود البيانات المطلوبة
            if (request.getUserId() == null || request.getAmount() == null) {
                return ResponseEntity.badRequest().body("User ID and amount are required");
            }

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // إنشاء سجل الدفع مع القيم الثابتة
            Payment payment = new Payment();
            payment.setUser(user);
            payment.setAmount(request.getAmount() / 100.0); // تحويل من سنت إلى دولار
            payment.setType("KINDERGARTEN");
            payment.setStatus("PAID");
            payment.setTransactionId("manual-" + System.currentTimeMillis());
            payment.setDate(LocalDate.now());

            // تعيين القيم الثابتة
            Property defaultProperty = propertyRepository.findById(8888888) // بدون L للنوع long
                    .orElseThrow(() -> new RuntimeException("Property not found with ID: 8888888"));
            payment.setProperty(defaultProperty);

            Payment savedPayment = paymentRepository.save(payment);

            return ResponseEntity.ok(Map.of(
                    "paymentId", savedPayment.getPaymentId(),
                    "status", "PAID",
                    "message", "Payment created successfully with fixed property_id "
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating payment: " + e.getMessage());
        }
    }

    @PostMapping("/enroll-child")
    public ResponseEntity<?> enrollChild(@RequestBody ChildEnrollmentRequest request) {
        try {
            // 1. التحقق من وجود الطفل
            Child child = childRepository.findById(request.getChildId().intValue())
                    .orElseThrow(() -> new RuntimeException("Child not found with ID: " + request.getChildId()));

            // 2. التحقق من وجود الحضانة
            Kindergarten kindergarten = kindergartenRepository.findById(request.getKindergartenId())
                    .orElseThrow(() -> new RuntimeException("Kindergarten not found with ID: " + request.getKindergartenId()));

            // 3. تحديث بيانات الطفل
            child.setKindergarten(kindergarten);
            childRepository.save(child);

            // 4. تحديث حالة الدفع (إذا كان paymentId موجودًا)
            if (request.getPaymentId() != null) {
                paymentRepository.findById(request.getPaymentId())
                        .ifPresent(p -> {
                            p.setStatus("PAID");
                            paymentRepository.save(p);
                        });
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Child enrolled successfully"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Enrollment failed: " + e.getMessage());
        }

}


    @PostMapping("/confirm-kindergarten")
    public String confirmKindergartenPayment(
            @RequestParam String paymentIntentId) {

        // 1. التحقق من نجاح الدفع مع Stripe
        // 2. إذا نجح الدفع، إنشاء التسجيل النهائي
        Enrollment enrollment = enrollmentService.createFinalEnrollment(paymentIntentId);

        return "Payment confirmed and enrollment created with ID: " + enrollment.getId();
    }

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest paymentRequest) {

        try {

            // 1. إنشاء PaymentIntent مع Stripe (بدون حفظf أي بيانات في DB)
            PaymentIntent intent = PaymentIntent.create(
                    new PaymentIntentCreateParams.Builder()
                            .setAmount(paymentRequest.getAmount()) // المبلغ بالسنترات (مثال: 1000 = 10.00 USD)
                            .setCurrency("ils") // أو "usd"
                            .setDescription(paymentRequest.getDescription())
                            .build()
            );

            // 2. إرجاع clientSecret فقط لفتح صفحة الدفع
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", intent.getClientSecret());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("فشل في عملية الدفع: " + e.getMessage());
        }
    }
}





