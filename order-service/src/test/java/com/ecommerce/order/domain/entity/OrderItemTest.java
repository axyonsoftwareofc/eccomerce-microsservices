// order-service/src/test/java/com/ecommerce/order/domain/entity/OrderItemTest.java
package com.ecommerce.order.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItem Entity Tests")
class OrderItemTest {

    @Test
    @DisplayName("Should calculate total price")
    void shouldCalculateTotalPrice() {
        OrderItem item = OrderItem.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .productName("Pizza")
                .quantity(2)
                .unitPrice(new BigDecimal("29.90"))
                .build();

        item.calculateTotalPrice();

        assertEquals(0, new BigDecimal("59.80").compareTo(item.getTotalPrice()));
    }

    @Test
    @DisplayName("Should create order item using factory method")
    void shouldCreateOrderItemUsingFactoryMethod() {
        OrderItem item = OrderItem.create(
                UUID.randomUUID(),
                "Pizza Margherita",
                2,
                new BigDecimal("35.00"),
                "Sem cebola"
        );

        assertNotNull(item.getId());
        assertEquals("Pizza Margherita", item.getProductName());
        assertEquals(2, item.getQuantity());
        assertEquals(0, new BigDecimal("70.00").compareTo(item.getTotalPrice()));
        assertEquals("Sem cebola", item.getNotes());
    }
}