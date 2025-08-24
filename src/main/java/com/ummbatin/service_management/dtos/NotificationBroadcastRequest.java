// NotificationBroadcastRequest.java
package com.ummbatin.service_management.dtos;

import lombok.Data;

@Data
public class NotificationBroadcastRequest {
    private String message;
    private String type;
}