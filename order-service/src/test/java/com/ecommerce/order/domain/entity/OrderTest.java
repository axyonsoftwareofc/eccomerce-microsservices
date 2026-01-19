// order-service/src/test/java/com/ecommerce/order/domain/entity/OrderTest.java
package com.ecommerce.order.domain.entity;

import com.ecommerce.order.domain.exception.InvalidOrderStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Entity Tests")
class OrderTest {

    private Order createOrder() {
        return Order.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .restaurantId(UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .deliveryStreet("Rua Teste")
                .deliveryNumber("123")
                .deliveryNeighborhood("Centro")
                .deliveryCity("São Paulo")
                .deliveryState("SP")
                .deliveryZipCode("01234-567")
                .subtotal(new BigDecimal("50.00"))
                .deliveryFee(new BigDecimal("5.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("55.00"))
                .build();
    }

    @Nested
    @DisplayName("Status Transitions")
    class StatusTransitionTests {

        @Test
        @DisplayName("Should confirm order from PENDING")
        void shouldConfirmOrderFromPending() {
            Order order = createOrder();

            order.confirm(30);

            assertEquals(OrderStatus.CONFIRMED, order.getStatus());
            assertEquals(30, order.getEstimatedDeliveryTime());
            assertNotNull(order.getConfirmedAt());
        }

        @Test
        @DisplayName("Should start preparing from CONFIRMED")
        void shouldStartPreparingFromConfirmed() {
            Order order = createOrder();
            order.confirm(30);

            order.startPreparing();

            assertEquals(OrderStatus.PREPARING, order.getStatus());
            assertNotNull(order.getPreparingAt());
        }

        @Test
        @DisplayName("Should mark as ready from PREPARING")
        void shouldMarkAsReadyFromPreparing() {
            Order order = createOrder();
            order.confirm(30);
            order.startPreparing();

            order.markAsReady();

            assertEquals(OrderStatus.READY, order.getStatus());
            assertNotNull(order.getReadyAt());
        }

        @Test
        @DisplayName("Should start delivery from READY")
        void shouldStartDeliveryFromReady() {
            Order order = createOrder();
            order.confirm(30);
            order.startPreparing();
            order.markAsReady();

            order.startDelivery();

            assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getStatus());
            assertNotNull(order.getPickedUpAt());
        }

        @Test
        @DisplayName("Should complete order from OUT_FOR_DELIVERY")
        void shouldCompleteOrderFromOutForDelivery() {
            Order order = createOrder();
            order.confirm(30);
            order.startPreparing();
            order.markAsReady();
            order.startDelivery();

            order.complete();

            assertEquals(OrderStatus.DELIVERED, order.getStatus());
            assertNotNull(order.getDeliveredAt());
        }

        @Test
        @DisplayName("Should throw exception for invalid transition")
        void shouldThrowExceptionForInvalidTransition() {
            Order order = createOrder();
            // Trying to complete directly from PENDING

            assertThrows(InvalidOrderStateException.class, order::complete);
        }
    }

    @Nested
    @DisplayName("Cancel Order")
    class CancelOrderTests {

        @Test
        @DisplayName("Should cancel order from PENDING")
        void shouldCancelOrderFromPending() {
            Order order = createOrder();

            order.cancel("Customer requested");

            assertEquals(OrderStatus.CANCELLED, order.getStatus());
            assertEquals("Customer requested", order.getCancellationReason());
            assertNotNull(order.getCancelledAt());
        }

        @Test
        @DisplayName("Should cancel order from CONFIRMED")
        void shouldCancelOrderFromConfirmed() {
            Order order = createOrder();
            order.confirm(30);

            order.cancel("Restaurant too busy");

            assertEquals(OrderStatus.CANCELLED, order.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when cancelling DELIVERED order")
        void shouldThrowExceptionWhenCancellingDeliveredOrder() {
            Order order = createOrder();
            order.confirm(30);
            order.startPreparing();
            order.markAsReady();
            order.startDelivery();
            order.complete();

            assertThrows(InvalidOrderStateException.class,
                    () -> order.cancel("Too late"));
        }

        @Test
        @DisplayName("Should throw exception when cancelling PREPARING order")
        void shouldThrowExceptionWhenCancellingPreparingOrder() {
            Order order = createOrder();
            order.confirm(30);
            order.startPreparing();

            assertThrows(InvalidOrderStateException.class,
                    () -> order.cancel("Changed mind"));
        }
    }

    @Nested
    @DisplayName("Order Status Checks")
    class OrderStatusChecksTests {

        @Test
        @DisplayName("Should return true when pending")
        void shouldReturnTrueWhenPending() {
            Order order = createOrder();
            assertTrue(order.isPending());
        }

        @Test
        @DisplayName("Should return true when active")
        void shouldReturnTrueWhenActive() {
            Order order = createOrder();
            order.confirm(30);
            assertTrue(order.isActive());
        }

        @Test
        @DisplayName("Should return false when delivered")
        void shouldReturnFalseWhenDelivered() {
            Order order = createOrder();
            order.confirm(30);
            order.startPreparing();
            order.markAsReady();
            order.startDelivery();
            order.complete();

            assertFalse(order.isActive());
        }

        @Test
        @DisplayName("Should return false when cancelled")
        void shouldReturnFalseWhenCancelled() {
            Order order = createOrder();
            order.cancel("Test");

            assertFalse(order.isActive());
        }
    }

    @Nested
    @DisplayName("Address Operations")
    class AddressOperationsTests {

        @Test
        @DisplayName("Should build full delivery address")
        void shouldBuildFullDeliveryAddress() {
            Order order = createOrder();
            order.setDeliveryComplement("Apto 101");

            String fullAddress = order.getFullDeliveryAddress();

            assertTrue(fullAddress.contains("Rua Teste"));
            assertTrue(fullAddress.contains("123"));
            assertTrue(fullAddress.contains("Apto 101"));
            assertTrue(fullAddress.contains("Centro"));
            assertTrue(fullAddress.contains("São Paulo"));
            assertTrue(fullAddress.contains("SP"));
        }

        @Test
        @DisplayName("Should handle null complement")
        void shouldHandleNullComplement() {
            Order order = createOrder();
            order.setDeliveryComplement(null);

            String fullAddress = order.getFullDeliveryAddress();

            assertFalse(fullAddress.contains("null"));
        }
    }
}