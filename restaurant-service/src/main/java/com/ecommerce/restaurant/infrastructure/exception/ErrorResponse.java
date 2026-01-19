// infrastructure/exception/ErrorResponse.java
package com.ecommerce.restaurant.infrastructure.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, LocalDateTime.now(), null);
    }

    public ErrorResponse withFieldErrors(Map<String, String> fieldErrors) {
        return new ErrorResponse(status, error, message, timestamp, fieldErrors);
    }
}