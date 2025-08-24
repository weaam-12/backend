package com.ummbatin.service_management.dtos;

import lombok.Data;

@Data
public class NotificationCreateRequest {
    private Long userId;
    private String message;
    private String type;
}