package com.ummbatin.service_management.dtos;

import java.time.LocalDate;

public class PaymentDto {
    private Long paymentId;
    private Long userId;
    private String userName;
    private String paymentType;
    private double amount;
    private String status;
    private LocalDate date; // تغيير من dueDate إلى date
    private LocalDate paymentDate;
    private String description;
    private String invoiceNumber;
    private String propertyAddress;

    // Constructors
    public PaymentDto() {
    }

    public PaymentDto(Long paymentId, Long userId, String paymentType, double amount, String status) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.status = status;
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate Date) {
        this.date = date;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }
}