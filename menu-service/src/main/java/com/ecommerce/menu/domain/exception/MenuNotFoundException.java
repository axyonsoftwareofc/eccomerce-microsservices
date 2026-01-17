// domain/exception/MenuNotFoundException.java
package com.ecommerce.menu.domain.exception;

import java.util.UUID;

public class MenuNotFoundException extends DomainException {

    public MenuNotFoundException(UUID id) {
        super("Menu not found with id: " + id);
    }

    public MenuNotFoundException(String sku) {
        super("Menu not found with SKU: " + sku);
    }
}