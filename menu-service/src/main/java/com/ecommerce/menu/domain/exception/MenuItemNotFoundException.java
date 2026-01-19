// domain/exception/MenuItemNotFoundException.java
package com.ecommerce.menu.domain.exception;

import java.util.UUID;

public class MenuItemNotFoundException extends DomainException {

    public MenuItemNotFoundException(UUID id) {
        super("Menu item not found with id: " + id);
    }
}