// order-service/src/test/java/com/ecommerce/order/application/service/OrderServiceTest.java
package com.ecommerce.order.application.service;

import com.ecommerce.order.application.dto.request.CreateOrderRequest;
import com.ecommerce.order.application.dto.request.OrderItemRequest;
import com.ecommerce.order.application.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.order.application.dto.response.OrderResponse;
import com.ecommerce.order.domain.entity.OrderStatus;
import com.ecommerce.order.domain.exception.InvalidOrderStateException;
import com.ecommerce.order.domain.exception.OrderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("OrderService Tests")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    private CreateOrderRequest createValidRequest() {
        List<OrderItemRequest> items = List.of(
                OrderItemRequest.builder()
                        .productId(UUID.randomUUID())
                        .productName("Pizza Margherita")
                        .quantity(2)
                        .unitPrice(new BigDecimal("45.90"))
                        .notes("Sem cebola")
                        .build(),
                OrderItemRequest.builder()
                        .productId(UUID.randomUUID())
                        .productName("Refrigerante 2L")
                        .quantity(1)
                        .unitPrice(new BigDecimal("12.00"))
                        .build()
        );

        return CreateOrderRequest.builder()
                .customerId(UUID.randomUUID())
                .restaurantId(UUID.randomUUID())
                .deliveryStreet("Rua das Flores")
                .deliveryNumber("123")
                .deliveryComplement("Apto 45")
                .deliveryNeighborhood("Centro")
                .deliveryCity("São Paulo")
                .deliveryState("SP")
                .deliveryZipCode("01234-567")
                .deliveryLatitude(new BigDecimal("-23.550520"))
                .deliveryLongitude(new BigDecimal("-46.633308"))
                .items(items)
                .notes("Tocar a campainha")
                .deliveryFee(new BigDecimal("5.00"))
                .build();
    }

    @Nested
    @DisplayName("Create Order")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order with valid data")
        void shouldCreateOrderWithValidData() {
            CreateOrderRequest request = createValidRequest();

            StepVerifier.create(orderService.createOrder(request))
                    .assertNext(response -> {
                        assert response.getId() != null;
                        assert response.getStatus() == OrderStatus.PENDING;
                        assert response.getItems().size() == 2;
                        assert response.getSubtotal().compareTo(new BigDecimal("103.80")) == 0; // (45.90 * 2) + 12.00
                        assert response.getDeliveryFee().compareTo(new BigDecimal("5.00")) == 0;
                        assert response.getTotal().compareTo(new BigDecimal("108.80")) == 0;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should calculate totals correctly")
        void shouldCalculateTotalsCorrectly() {
            CreateOrderRequest request = createValidRequest();

            StepVerifier.create(orderService.createOrder(request))
                    .assertNext(response -> {
                        // Pizza: 45.90 * 2 = 91.80
                        // Refrigerante: 12.00 * 1 = 12.00
                        // Subtotal: 103.80
                        // Delivery: 5.00
                        // Total: 108.80
                        assert response.getSubtotal().compareTo(new BigDecimal("103.80")) == 0;
                        assert response.getTotal().compareTo(new BigDecimal("108.80")) == 0;
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Get Order")
    class GetOrderTests {

        @Test
        @DisplayName("Should get order by ID")
        void shouldGetOrderById() {
            CreateOrderRequest request = createValidRequest();

            StepVerifier.create(
                            orderService.createOrder(request)
                                    .flatMap(created -> orderService.getOrderById(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getStatus() == OrderStatus.PENDING;
                        assert response.getItems().size() == 2;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should throw exception when order not found")
        void shouldThrowExceptionWhenOrderNotFound() {
            UUID nonExistentId = UUID.randomUUID();

            StepVerifier.create(orderService.getOrderById(nonExistentId))
                    .expectError(OrderNotFoundException.class)
                    .verify();
        }

        @Test
        @DisplayName("Should get orders by customer")
        void shouldGetOrdersByCustomer() {
            UUID customerId = UUID.randomUUID();
            CreateOrderRequest request = createValidRequest();
            request.setCustomerId(customerId);

            StepVerifier.create(
                            orderService.createOrder(request)
                                    .thenMany(orderService.getOrdersByCustomer(customerId))
                                    .collectList()
                    )
                    .assertNext(orders -> {
                        assert orders.size() >= 1;
                        assert orders.stream().allMatch(o -> o.getCustomerId().equals(customerId));
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should get orders by restaurant")
        void shouldGetOrdersByRestaurant() {
            UUID restaurantId = UUID.randomUUID();
            CreateOrderRequest request = createValidRequest();
            request.setRestaurantId(restaurantId);

            StepVerifier.create(
                            orderService.createOrder(request)
                                    .thenMany(orderService.getOrdersByRestaurant(restaurantId))
                                    .collectList()
                    )
                    .assertNext(orders -> {
                        assert orders.size() >= 1;
                        assert orders.stream().allMatch(o -> o.getRestaurantId().equals(restaurantId));
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should get orders by status")
        void shouldGetOrdersByStatus() {
            CreateOrderRequest request = createValidRequest();

            StepVerifier.create(
                            orderService.createOrder(request)
                                    .thenMany(orderService.getOrdersByStatus(OrderStatus.PENDING))
                                    .collectList()
                    )
                    .assertNext(orders -> {
                        assert orders.stream().allMatch(o -> o.getStatus() == OrderStatus.PENDING);
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Update Order Status")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("Should confirm order")
        void shouldConfirmOrder() {
            CreateOrderRequest request = createValidRequest();
            UpdateOrderStatusRequest statusRequest = UpdateOrderStatusRequest.builder()
                    .status(OrderStatus.CONFIRMED)
                    .estimatedDeliveryTime(45)
                    .build();

            StepVerifier.create(
                            orderService.createOrder(request)
                                    .flatMap(created ->
                                            orderService.updateOrderStatus(created.getId(), statusRequest))
                    )
                    .assertNext(response -> {
                        assert response.getStatus() == OrderStatus.CONFIRMED;
                        assert response.getEstimatedDeliveryTime() == 45;
                        assert response.getConfirmedAt() != null;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should start preparing order")
        void shouldStartPreparingOrder() {
            CreateOrderRequest request = createValidRequest();

            StepVerifier.create(
                            orderService.createOrder(request)
                                    .flatMap(created -> orderService.updateOrderStatus(
                                            created.getId(),
                                            UpdateOrderStatusRequest.builder()
                                                    .status(OrderStatus.CONFIRMED)
                                                    .estimatedDeliveryTime(45)
                                                    .build()
                                    ))
                                    .flatMap(confirmed -> orderService.updateOrderStatus(
                                            confirmed.getId(),
                                            UpdateOrderStatusRequest.builder()
                                                    .status(OrderStatus.PREPARING)
                                                    .build()
                                    ))
                    )
                    .assertNext(response -> {
                        assert response.getStatus() == OrderStatus.PREPARING;
                        assert response.getPreparingAt() != null;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should complete full order flow")
        void shouldCompleteFullOrderFlow() {
            CreateOrderRequest request = createValidRequest();

            StepVerifier.create(
                            orderService.createOrder(request)
                                    .flatMap(created -> orderService.updateOrderStatus(
                                            created.getId(),
                                            UpdateOrderStatusRequest.builder()
                                                    .status(OrderStatus.CONFIRMED)
                                                    .estimatedDeliveryTime(45)
                                                    .build()
                                    ))
                                    .flatMap(confirmed -> orderService.updateOrderStatus(
                                            confirmed.getId(),
                                            UpdateOrderStatusRequest.builder().status(OrderStatus.PREPARING).build()
                                    ))
                                    .flatMap(preparing -> orderService.updateOrderStatus(
                                            preparing.getId(),
                                            UpdateOrderStatusRequest.builder().status(OrderStatus.READY).build()
                                    ))
                                    .flatMap(ready -> orderService.updateOrderStatus(
                                            ready.getId(),
                                            UpdateOrderStatusRequest.builder().status(OrderStatus.OUT_FOR_DELIVERY).build()
                                    ))
                                    .flatMap(outForDelivery -> orderService.updateOrderStatus(
                                            outForDelivery.getId(),
                                            UpdateOrderStatusRequest.builder().status(OrderStatus.DELIVERED).build()
                                    ))
                    )
                    .assertNext(response -> {
                        assert response.getStatus() == OrderStatus.DELIVERED;
                        assert response.getDeliveredAt() != null;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should throw exception for invalid status transition")
        void shouldThrowExceptionForInvalidStatusTransition() {
            CreateOrderRequest request = createValidRequest();

            StepVerifier.create(
                            orderService.createOrder(request)
                                    .flatMap(created -> orderService.updateOrderStatus(
                                            created.getId(),
                                            UpdateOrderStatusRequest.builder()
                                                    .status(OrderStatus.DELIVERED) // Invalid: PENDING -> DELIVERED
                                                    .build()
                                    ))
                    )
                    .expectError(InvalidOrderStateException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Cancel Order")
    class CancelOrderTests {

        @Test
        @DisplayName("Should cancel pending order")
        void shouldCancelPendingOrder() {
            CreateOrderRequest request = createValidRequest();

            StepVerifier.create(
                            orderService.createOrder(request)
                                    .flatMap(created ->
                                            orderService.cancelOrder(created.getId(), "Customer request"))
                    )
                    .assertNext(response -> {
                        assert response.getStatus() == OrderStatus.CANCELLED;
                        assert response.getCancellationReason().equals("Customer request");
                        assert response.getCancelledAt() != null;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should cancel confirmed order")
        void shouldCancelConfirmedOrder() {
            CreateOrderRequest request = createValidRequest();

            StepVerifier.create(
                            orderService.createOrder(request)
                                    .flatMap(created -> orderService.updateOrderStatus(
                                            created.getId(),
                                            UpdateOrderStatusRequest.builder()
                                                    .status(OrderStatus.CONFIRMED)
                                                    .estimatedDeliveryTime(45)
                                                    .build()
                                    ))
                                    .flatMap(confirmed ->
                                            orderService.cancelOrder(confirmed.getId(), "Restaurant closed"))
                    )
                    .assertNext(response -> {
                        assert response.getStatus() == OrderStatus.CANCELLED;
                        assert response.getCancellationReason().equals("Restaurant closed");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should not cancel order in PREPARING status")
        void shouldNotCancelOrderInPreparingStatus() {
            CreateOrderRequest request = createValidRequest();

            StepVerifier.create(
                            orderService.createOrder(request)
                                    .flatMap(created -> orderService.updateOrderStatus(
                                            created.getId(),
                                            UpdateOrderStatusRequest.builder()
                                                    .status(OrderStatus.CONFIRMED)
                                                    .estimatedDeliveryTime(45)
                                                    .build()
                                    ))
                                    .flatMap(confirmed -> orderService.updateOrderStatus(
                                            confirmed.getId(),
                                            UpdateOrderStatusRequest.builder().status(OrderStatus.PREPARING).build()
                                    ))
                                    .flatMap(preparing ->
                                            orderService.cancelOrder(preparing.getId(), "Too late"))
                    )
                    .expectError(InvalidOrderStateException.class)
                    .verify();
        }
    }
}