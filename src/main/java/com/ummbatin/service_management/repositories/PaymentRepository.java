package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.Payment;
import com.ummbatin.service_management.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByUser_UserId(Long userId);
    List<Payment> findByProperty_PropertyId(Integer propertyId);
    List<Payment> findByStatus(String status);
    List<Payment> findByUser_UserIdAndPaymentDateAfter(Long userId, LocalDateTime cutoffDate);
    List<Payment> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    @Query("SELECT p FROM Payment p JOIN p.user u LEFT JOIN u.properties prop WHERE p.date BETWEEN :startDate AND :endDate")
    List<Payment> findPaymentsWithUserAndPropertiesBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    Optional<Payment> findByUser_UserIdAndServiceIdAndTransactionId(
            Long userId,
            Long serviceId,
            String transactionId
    );
    List<Payment> findByUser_UserIdAndTypeAndDateBetween(
            Long userId,
            String type,
            LocalDate startDate,
            LocalDate endDate
    );
    boolean existsByUserAndTypeAndDateBetween(
            User user,
            String type,
            LocalDate startDate,
            LocalDate endDate);
    List<Payment> findByType(String type);
    List<Payment> findByUser_UserIdAndType(Long userId, String type);
}