// domain/exception/RestaurantNotFoundException.java
package com.ecommerce.restaurant.domain.exception;

import java.util.UUID;

public class RestaurantNotFoundException extends DomainException {

    public RestaurantNotFoundException(UUID id) {
        super("Restaurant not found with id: " + id);
    }

    public RestaurantNotFoundException(String message) {
        super(message);
    }
}