package com.ummbatin.service_management.dtos;

public class ErrorResponseDto {
    private String message;
    private String details;

    public ErrorResponseDto(String message, String details) {
        this.message = message;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
