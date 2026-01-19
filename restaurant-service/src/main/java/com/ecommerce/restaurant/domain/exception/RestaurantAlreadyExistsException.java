// domain/exception/RestaurantAlreadyExistsException.java
package com.ecommerce.restaurant.domain.exception;

import java.util.UUID;

public class RestaurantAlreadyExistsException extends DomainException {

    public RestaurantAlreadyExistsException(String name, UUID ownerId) {
        super("Restaurant '" + name + "' already exists for this owner");
    }
}