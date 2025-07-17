package com.ummbatin.service_management.dtos;

public class UpdatePaymentStatusRequest {
    private String status;

    // Default constructor
    public UpdatePaymentStatusRequest() {
    }

    // Constructor with status
    public UpdatePaymentStatusRequest(String status) {
        this.status = status;
    }

    // Getter and Setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}