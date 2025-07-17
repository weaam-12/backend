package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByUser_UserId(Long userId);
    List<Payment> findByProperty_PropertyId(Integer propertyId);
    List<Payment> findByStatus(String status);
    List<Payment> findByUser_UserIdAndPaymentDateAfter(Long userId, LocalDateTime cutoffDate);

    Optional<Payment> findByUser_UserIdAndServiceIdAndTransactionId(
            Long userId,
            Long serviceId,
            String transactionId
    );
}