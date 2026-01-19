package com.ecommerce.order.domain.exception;

import java.util.UUID;

public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(UUID id) {
        super("Order not found with id: " + id);
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}