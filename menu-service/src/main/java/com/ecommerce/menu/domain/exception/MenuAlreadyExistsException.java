// domain/exception/MenuAlreadyExistsException.java
package com.ecommerce.menu.domain.exception;

public class MenuAlreadyExistsException extends DomainException {

    public MenuAlreadyExistsException(String sku) {
        super("Menu already exists with SKU: " + sku);
    }
}