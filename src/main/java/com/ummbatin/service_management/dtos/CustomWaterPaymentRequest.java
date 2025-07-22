package com.ummbatin.service_management.dtos;

public class CustomWaterPaymentRequest {
    private Long userId;
    private Double amount;
    private Double lastReading;
    private Double currentReading;
    private Double consumption;
    private Long propertyId; // أضفنا propertyId
    private Boolean manual; // تأكد من وجود هذا الحقل
    public Boolean getManual() { return manual; } // هذه الدالة المطلوبة
    public void setManual(Boolean manual) { this.manual = manual; }
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Double getLastReading() { return lastReading; }
    public void setLastReading(Double lastReading) { this.lastReading = lastReading; }
    public Double getCurrentReading() { return currentReading; }
    public void setCurrentReading(Double currentReading) { this.currentReading = currentReading; }
    public Double getConsumption() { return consumption; }
    public void setConsumption(Double consumption) { this.consumption = consumption; }
    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }
}