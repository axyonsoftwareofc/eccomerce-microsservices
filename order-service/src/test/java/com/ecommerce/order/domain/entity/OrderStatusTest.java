// order-service/src/test/java/com/ecommerce/order/domain/entity/OrderStatusTest.java
package com.ecommerce.order.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderStatus Tests")
class OrderStatusTest {

    @Test
    @DisplayName("PENDING can transition to CONFIRMED")
    void pendingCanTransitionToConfirmed() {
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CONFIRMED));
    }

    @Test
    @DisplayName("PENDING can transition to CANCELLED")
    void pendingCanTransitionToCancelled() {
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED));
    }

    @Test
    @DisplayName("PENDING cannot transition to DELIVERED")
    void pendingCannotTransitionToDelivered() {
        assertFalse(OrderStatus.PENDING.canTransitionTo(OrderStatus.DELIVERED));
    }

    @Test
    @DisplayName("DELIVERED is final")
    void deliveredIsFinal() {
        assertTrue(OrderStatus.DELIVERED.isFinal());
    }

    @Test
    @DisplayName("CANCELLED is final")
    void cancelledIsFinal() {
        assertTrue(OrderStatus.CANCELLED.isFinal());
    }

    @Test
    @DisplayName("PREPARING is not final")
    void preparingIsNotFinal() {
        assertFalse(OrderStatus.PREPARING.isFinal());
    }

    @Test
    @DisplayName("PENDING can be cancelled")
    void pendingCanBeCancelled() {
        assertTrue(OrderStatus.PENDING.canBeCancelled());
    }

    @Test
    @DisplayName("CONFIRMED can be cancelled")
    void confirmedCanBeCancelled() {
        assertTrue(OrderStatus.CONFIRMED.canBeCancelled());
    }

    @Test
    @DisplayName("PREPARING cannot be cancelled")
    void preparingCannotBeCancelled() {
        assertFalse(OrderStatus.PREPARING.canBeCancelled());
    }
}