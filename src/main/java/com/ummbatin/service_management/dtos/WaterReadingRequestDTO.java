// WaterBillRequestDTO.java
package com.ummbatin.service_management.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WaterReadingRequestDTO {
    private Long propertyId;
    private Double reading;   // قراءة العداد - نفس اسم الحقل في الجدول
    private LocalDateTime date;
    private Double amount;    // المبلغ الإجمالي

}