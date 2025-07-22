package com.ummbatin.service_management.dtos;

public class UserWithPaymentsDto {
    private Long userId;
    private String userName;
    private Long propertyId; // أضفنا propertyId
    private Double waterAmount;
    private Integer waterPaymentId;
    private String waterStatus;
    private Double arnonaAmount;
    private Integer arnonaPaymentId;
    private String arnonaStatus;
    private String propertyAddress;
    private Double lastWaterReading; // آخر قراءة مياه
    private Boolean manual; // هل القراءة يدوية
    public Boolean getManual() { return manual; } // هذه الدالة المطلوبة
    public void setManual(Boolean manual) { this.manual = manual; }
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }
    public Double getWaterAmount() { return waterAmount; }
    public void setWaterAmount(Double waterAmount) { this.waterAmount = waterAmount; }
    public Integer getWaterPaymentId() { return waterPaymentId; }
    public void setWaterPaymentId(Integer waterPaymentId) { this.waterPaymentId = waterPaymentId; }
    public String getWaterStatus() { return waterStatus; }
    public void setWaterStatus(String waterStatus) { this.waterStatus = waterStatus; }
    public Double getArnonaAmount() { return arnonaAmount; }
    public void setArnonaAmount(Double arnonaAmount) { this.arnonaAmount = arnonaAmount; }
    public Integer getArnonaPaymentId() { return arnonaPaymentId; }
    public void setArnonaPaymentId(Integer arnonaPaymentId) { this.arnonaPaymentId = arnonaPaymentId; }
    public String getArnonaStatus() { return arnonaStatus; }
    public void setArnonaStatus(String arnonaStatus) { this.arnonaStatus = arnonaStatus; }
    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }
    public Double getLastWaterReading() { return lastWaterReading; }
    public void setLastWaterReading(Double lastWaterReading) { this.lastWaterReading = lastWaterReading; }

}