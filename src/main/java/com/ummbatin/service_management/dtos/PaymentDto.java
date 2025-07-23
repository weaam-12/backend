package com.ummbatin.service_management.dtos;

import java.time.LocalDate;

public class PaymentDto {
    private Long paymentId;
    private Long userId;
    private String userName;
    private String paymentType;
    private double amount;
    private String status;
    private LocalDate date;
    private LocalDate paymentDate;
    private String propertyAddress;
    private double propertyArea;
    private int propertyUnits;

    // Constructors
    public PaymentDto() {}
    public PaymentDto(Long paymentId, Long userId, String userName, String paymentType,
                      double amount, String status, LocalDate date, String propertyAddress,
                      double propertyArea, int propertyUnits) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.userName = userName;
        this.paymentType = paymentType;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.propertyAddress = propertyAddress;
        this.propertyArea = propertyArea;
        this.propertyUnits = propertyUnits;
    }

    // Getters & Setters
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }
    public double getPropertyArea() { return propertyArea; }
    public void setPropertyArea(double propertyArea) { this.propertyArea = propertyArea; }
    public int getPropertyUnits() { return propertyUnits; }
    public void setPropertyUnits(int propertyUnits) { this.propertyUnits = propertyUnits; }
}