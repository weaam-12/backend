package com.ummbatin.service_management.dtos;

import lombok.Data;

@Data
public class EnrollmentRequestDTO {
    private Long childId;
    private Integer kindergartenId;
    private String status;
    private String PaymentIntentId;



    public String getPaymentIntentId() {
        return PaymentIntentId;
    }
}