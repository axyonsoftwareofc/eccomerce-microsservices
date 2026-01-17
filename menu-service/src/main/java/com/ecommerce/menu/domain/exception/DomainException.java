// domain/exception/DomainException.java
package com.ecommerce.menu.domain.exception;

public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }
}