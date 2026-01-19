// domain/exception/CategoryNotFoundException.java
package com.ecommerce.menu.domain.exception;

import java.util.UUID;

public class CategoryNotFoundException extends DomainException {

    public CategoryNotFoundException(UUID id) {
        super("Category not found with id: " + id);
    }
}