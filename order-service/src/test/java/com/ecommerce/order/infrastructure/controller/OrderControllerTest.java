// order-service/src/test/java/com/ecommerce/order/infrastructure/controller/OrderControllerTest.java
package com.ecommerce.order.infrastructure.controller;

import com.ecommerce.order.application.dto.request.CreateOrderRequest;
import com.ecommerce.order.application.dto.request.OrderItemRequest;
import com.ecommerce.order.application.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.order.application.dto.response.OrderItemResponse;
import com.ecommerce.order.application.dto.response.OrderResponse;
import com.ecommerce.order.application.service.OrderService;
import com.ecommerce.order.domain.entity.OrderStatus;
import com.ecommerce.order.domain.exception.InvalidOrderStateException;
import com.ecommerce.order.domain.exception.OrderNotFoundException;
import com.ecommerce.order.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderController Tests")
class OrderControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private OrderResponse sampleOrderResponse;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
                .bindToController(orderController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();

        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();

        sampleOrderResponse = OrderResponse.builder()
                .id(orderId)
                .customerId(customerId)
                .restaurantId(restaurantId)
                .status(OrderStatus.PENDING)
                .deliveryStreet("Rua das Flores")
                .deliveryNumber("123")
                .deliveryNeighborhood("Centro")
                .deliveryCity("São Paulo")
                .deliveryState("SP")
                .deliveryZipCode("01234-567")
                .fullDeliveryAddress("Rua das Flores, 123, Centro, São Paulo - SP, 01234-567")
                .subtotal(new BigDecimal("103.80"))
                .deliveryFee(new BigDecimal("5.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("108.80"))
                .items(List.of(
                        OrderItemResponse.builder()
                                .id(UUID.randomUUID())
                                .productId(UUID.randomUUID())
                                .productName("Pizza Margherita")
                                .quantity(2)
                                .unitPrice(new BigDecimal("45.90"))
                                .totalPrice(new BigDecimal("91.80"))
                                .build()
                ))
                .createdAt(LocalDateTime.now())
                .build();

        createOrderRequest = CreateOrderRequest.builder()
                .customerId(customerId)
                .restaurantId(restaurantId)
                .deliveryStreet("Rua das Flores")
                .deliveryNumber("123")
                .deliveryNeighborhood("Centro")
                .deliveryCity("São Paulo")
                .deliveryState("SP")
                .deliveryZipCode("01234-567")
                .items(List.of(
                        OrderItemRequest.builder()
                                .productId(UUID.randomUUID())
                                .productName("Pizza Margherita")
                                .quantity(2)
                                .unitPrice(new BigDecimal("45.90"))
                                .build()
                ))
                .deliveryFee(new BigDecimal("5.00"))
                .build();
    }

    @Nested
    @DisplayName("POST /api/v1/orders")
    class CreateOrderEndpoint {

        @Test
        @DisplayName("Should create order and return 201")
        void shouldCreateOrderAndReturn201() {
            when(orderService.createOrder(any(CreateOrderRequest.class)))
                    .thenReturn(Mono.just(sampleOrderResponse));

            webTestClient.post()
                    .uri("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createOrderRequest)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(orderId.toString())
                    .jsonPath("$.status").isEqualTo("PENDING")
                    .jsonPath("$.customerId").isEqualTo(customerId.toString())
                    .jsonPath("$.total").isEqualTo(108.80);
        }

        @Test
        @DisplayName("Should return 400 for missing customerId")
        void shouldReturn400ForMissingCustomerId() {
            CreateOrderRequest invalidRequest = CreateOrderRequest.builder()
                    .restaurantId(restaurantId)
                    .deliveryStreet("Rua das Flores")
                    .deliveryNumber("123")
                    .deliveryNeighborhood("Centro")
                    .deliveryCity("São Paulo")
                    .deliveryState("SP")
                    .deliveryZipCode("01234-567")
                    .items(List.of(
                            OrderItemRequest.builder()
                                    .productId(UUID.randomUUID())
                                    .productName("Pizza")
                                    .quantity(1)
                                    .unitPrice(new BigDecimal("45.90"))
                                    .build()
                    ))
                    .build();

            webTestClient.post()
                    .uri("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidRequest)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.fieldErrors.customerId").isEqualTo("Customer ID is required");
        }

        @Test
        @DisplayName("Should return 400 for empty items")
        void shouldReturn400ForEmptyItems() {
            CreateOrderRequest requestWithNoItems = CreateOrderRequest.builder()
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .deliveryStreet("Rua das Flores")
                    .deliveryNumber("123")
                    .deliveryNeighborhood("Centro")
                    .deliveryCity("São Paulo")
                    .deliveryState("SP")
                    .deliveryZipCode("01234-567")
                    .items(List.of())
                    .build();

            webTestClient.post()
                    .uri("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestWithNoItems)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.fieldErrors.items").isEqualTo("Order must have at least one item");
        }

        @Test
        @DisplayName("Should return 400 for missing delivery address")
        void shouldReturn400ForMissingDeliveryAddress() {
            CreateOrderRequest requestWithNoAddress = CreateOrderRequest.builder()
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .items(List.of(
                            OrderItemRequest.builder()
                                    .productId(UUID.randomUUID())
                                    .productName("Pizza")
                                    .quantity(1)
                                    .unitPrice(new BigDecimal("45.90"))
                                    .build()
                    ))
                    .build();

            webTestClient.post()
                    .uri("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestWithNoAddress)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/{id}")
    class GetOrderByIdEndpoint {

        @Test
        @DisplayName("Should return order by ID")
        void shouldReturnOrderById() {
            when(orderService.getOrderById(orderId))
                    .thenReturn(Mono.just(sampleOrderResponse));

            webTestClient.get()
                    .uri("/api/v1/orders/{id}", orderId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(orderId.toString())
                    .jsonPath("$.status").isEqualTo("PENDING")
                    .jsonPath("$.items").isArray()
                    .jsonPath("$.items.length()").isEqualTo(1);
        }

        @Test
        @DisplayName("Should return 404 when order not found")
        void shouldReturn404WhenOrderNotFound() {
            UUID nonExistentId = UUID.randomUUID();
            when(orderService.getOrderById(nonExistentId))
                    .thenReturn(Mono.error(new OrderNotFoundException(nonExistentId)));

            webTestClient.get()
                    .uri("/api/v1/orders/{id}", nonExistentId)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.message").value(msg ->
                            ((String) msg).contains("Order not found"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/customer/{customerId}")
    class GetOrdersByCustomerEndpoint {

        @Test
        @DisplayName("Should return orders by customer")
        void shouldReturnOrdersByCustomer() {
            when(orderService.getOrdersByCustomer(customerId))
                    .thenReturn(Flux.just(sampleOrderResponse));

            webTestClient.get()
                    .uri("/api/v1/orders/customer/{customerId}", customerId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(OrderResponse.class)
                    .hasSize(1);
        }

        @Test
        @DisplayName("Should return empty list when no orders for customer")
        void shouldReturnEmptyListWhenNoOrders() {
            UUID newCustomerId = UUID.randomUUID();
            when(orderService.getOrdersByCustomer(newCustomerId))
                    .thenReturn(Flux.empty());

            webTestClient.get()
                    .uri("/api/v1/orders/customer/{customerId}", newCustomerId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(OrderResponse.class)
                    .hasSize(0);
        }

        @Test
        @DisplayName("Should return multiple orders for customer")
        void shouldReturnMultipleOrdersForCustomer() {
            OrderResponse secondOrder = OrderResponse.builder()
                    .id(UUID.randomUUID())
                    .customerId(customerId)
                    .restaurantId(UUID.randomUUID())
                    .status(OrderStatus.DELIVERED)
                    .total(new BigDecimal("55.00"))
                    .items(List.of())
                    .build();

            when(orderService.getOrdersByCustomer(customerId))
                    .thenReturn(Flux.just(sampleOrderResponse, secondOrder));

            webTestClient.get()
                    .uri("/api/v1/orders/customer/{customerId}", customerId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(OrderResponse.class)
                    .hasSize(2);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/restaurant/{restaurantId}")
    class GetOrdersByRestaurantEndpoint {

        @Test
        @DisplayName("Should return orders by restaurant")
        void shouldReturnOrdersByRestaurant() {
            when(orderService.getOrdersByRestaurant(restaurantId))
                    .thenReturn(Flux.just(sampleOrderResponse));

            webTestClient.get()
                    .uri("/api/v1/orders/restaurant/{restaurantId}", restaurantId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(OrderResponse.class)
                    .hasSize(1);
        }

        @Test
        @DisplayName("Should return active orders by restaurant")
        void shouldReturnActiveOrdersByRestaurant() {
            when(orderService.getActiveOrdersByRestaurant(restaurantId))
                    .thenReturn(Flux.just(sampleOrderResponse));

            webTestClient.get()
                    .uri("/api/v1/orders/restaurant/{restaurantId}/active", restaurantId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(OrderResponse.class)
                    .hasSize(1);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/status/{status}")
    class GetOrdersByStatusEndpoint {

        @Test
        @DisplayName("Should return orders by status PENDING")
        void shouldReturnOrdersByStatusPending() {
            when(orderService.getOrdersByStatus(OrderStatus.PENDING))
                    .thenReturn(Flux.just(sampleOrderResponse));

            webTestClient.get()
                    .uri("/api/v1/orders/status/{status}", "PENDING")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(OrderResponse.class)
                    .hasSize(1);
        }

        @Test
        @DisplayName("Should return orders by status PREPARING")
        void shouldReturnOrdersByStatusPreparing() {
            OrderResponse preparingOrder = OrderResponse.builder()
                    .id(UUID.randomUUID())
                    .status(OrderStatus.PREPARING)
                    .items(List.of())
                    .build();

            when(orderService.getOrdersByStatus(OrderStatus.PREPARING))
                    .thenReturn(Flux.just(preparingOrder));

            webTestClient.get()
                    .uri("/api/v1/orders/status/{status}", "PREPARING")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(OrderResponse.class)
                    .hasSize(1);
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/orders/{id}/status")
    class UpdateOrderStatusEndpoint {

        @Test
        @DisplayName("Should update order status to CONFIRMED")
        void shouldUpdateOrderStatusToConfirmed() {
            OrderResponse confirmedResponse = OrderResponse.builder()
                    .id(orderId)
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .status(OrderStatus.CONFIRMED)
                    .estimatedDeliveryTime(45)
                    .confirmedAt(LocalDateTime.now())
                    .items(List.of())
                    .build();

            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                    .status(OrderStatus.CONFIRMED)
                    .estimatedDeliveryTime(45)
                    .build();

            when(orderService.updateOrderStatus(eq(orderId), any(UpdateOrderStatusRequest.class)))
                    .thenReturn(Mono.just(confirmedResponse));

            webTestClient.patch()
                    .uri("/api/v1/orders/{id}/status", orderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("CONFIRMED")
                    .jsonPath("$.estimatedDeliveryTime").isEqualTo(45)
                    .jsonPath("$.confirmedAt").isNotEmpty();
        }

        @Test
        @DisplayName("Should update order status to PREPARING")
        void shouldUpdateOrderStatusToPreparing() {
            OrderResponse preparingResponse = OrderResponse.builder()
                    .id(orderId)
                    .status(OrderStatus.PREPARING)
                    .preparingAt(LocalDateTime.now())
                    .items(List.of())
                    .build();

            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                    .status(OrderStatus.PREPARING)
                    .build();

            when(orderService.updateOrderStatus(eq(orderId), any(UpdateOrderStatusRequest.class)))
                    .thenReturn(Mono.just(preparingResponse));

            webTestClient.patch()
                    .uri("/api/v1/orders/{id}/status", orderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("PREPARING");
        }

        @Test
        @DisplayName("Should return 409 CONFLICT for invalid status transition")
        void shouldReturn409ForInvalidStatusTransition() {
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                    .status(OrderStatus.DELIVERED)
                    .build();

            when(orderService.updateOrderStatus(eq(orderId), any(UpdateOrderStatusRequest.class)))
                    .thenReturn(Mono.error(new InvalidOrderStateException(
                            "Cannot transition from PENDING to DELIVERED")));

            webTestClient.patch()
                    .uri("/api/v1/orders/{id}/status", orderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(409)
                    .expectBody()
                    .jsonPath("$.message").isEqualTo("Cannot transition from PENDING to DELIVERED");
        }

        @Test
        @DisplayName("Should return 404 when order not found for status update")
        void shouldReturn404WhenOrderNotFoundForStatusUpdate() {
            UUID nonExistentId = UUID.randomUUID();
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                    .status(OrderStatus.CONFIRMED)
                    .estimatedDeliveryTime(45)
                    .build();

            when(orderService.updateOrderStatus(eq(nonExistentId), any(UpdateOrderStatusRequest.class)))
                    .thenReturn(Mono.error(new OrderNotFoundException(nonExistentId)));

            webTestClient.patch()
                    .uri("/api/v1/orders/{id}/status", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("Should return 400 for missing status in request")
        void shouldReturn400ForMissingStatus() {
            webTestClient.patch()
                    .uri("/api/v1/orders/{id}/status", orderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("{}")
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("POST /api/v1/orders/{id}/cancel")
    class CancelOrderEndpoint {

        @Test
        @DisplayName("Should cancel pending order with reason")
        void shouldCancelPendingOrderWithReason() {
            OrderResponse cancelledResponse = OrderResponse.builder()
                    .id(orderId)
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .status(OrderStatus.CANCELLED)
                    .cancellationReason("Customer request")
                    .cancelledAt(LocalDateTime.now())
                    .items(List.of())
                    .build();

            when(orderService.cancelOrder(eq(orderId), eq("Customer request")))
                    .thenReturn(Mono.just(cancelledResponse));

            webTestClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/orders/{id}/cancel")
                            .queryParam("reason", "Customer request")
                            .build(orderId))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("CANCELLED")
                    .jsonPath("$.cancellationReason").isEqualTo("Customer request")
                    .jsonPath("$.cancelledAt").isNotEmpty();
        }

        @Test
        @DisplayName("Should cancel order without reason")
        void shouldCancelOrderWithoutReason() {
            OrderResponse cancelledResponse = OrderResponse.builder()
                    .id(orderId)
                    .status(OrderStatus.CANCELLED)
                    .cancelledAt(LocalDateTime.now())
                    .items(List.of())
                    .build();

            when(orderService.cancelOrder(eq(orderId), eq(null)))
                    .thenReturn(Mono.just(cancelledResponse));

            webTestClient.post()
                    .uri("/api/v1/orders/{id}/cancel", orderId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("CANCELLED");
        }

        @Test
        @DisplayName("Should return 409 CONFLICT when cancelling order in PREPARING status")
        void shouldReturn409WhenCancellingOrderInPreparingStatus() {
            when(orderService.cancelOrder(eq(orderId), any()))
                    .thenReturn(Mono.error(new InvalidOrderStateException(
                            "Order cannot be cancelled in status: PREPARING")));

            webTestClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/orders/{id}/cancel")
                            .queryParam("reason", "Too late")
                            .build(orderId))
                    .exchange()
                    .expectStatus().isEqualTo(409)
                    .expectBody()
                    .jsonPath("$.message").isEqualTo("Order cannot be cancelled in status: PREPARING");
        }

        @Test
        @DisplayName("Should return 409 when cancelling delivered order")
        void shouldReturn409WhenCancellingDeliveredOrder() {
            when(orderService.cancelOrder(eq(orderId), any()))
                    .thenReturn(Mono.error(new InvalidOrderStateException(
                            "Order cannot be cancelled in status: DELIVERED")));

            webTestClient.post()
                    .uri("/api/v1/orders/{id}/cancel", orderId)
                    .exchange()
                    .expectStatus().isEqualTo(409);
        }

        @Test
        @DisplayName("Should return 404 when cancelling non-existent order")
        void shouldReturn404WhenCancellingNonExistentOrder() {
            UUID nonExistentId = UUID.randomUUID();

            when(orderService.cancelOrder(eq(nonExistentId), any()))
                    .thenReturn(Mono.error(new OrderNotFoundException(nonExistentId)));

            webTestClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/orders/{id}/cancel")
                            .queryParam("reason", "Test")
                            .build(nonExistentId))
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate item quantity - zero not allowed")
        void shouldValidateItemQuantityZero() {
            CreateOrderRequest requestWithZeroQuantity = CreateOrderRequest.builder()
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .deliveryStreet("Rua das Flores")
                    .deliveryNumber("123")
                    .deliveryNeighborhood("Centro")
                    .deliveryCity("São Paulo")
                    .deliveryState("SP")
                    .deliveryZipCode("01234-567")
                    .items(List.of(
                            OrderItemRequest.builder()
                                    .productId(UUID.randomUUID())
                                    .productName("Pizza")
                                    .quantity(0)
                                    .unitPrice(new BigDecimal("45.90"))
                                    .build()
                    ))
                    .build();

            webTestClient.post()
                    .uri("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestWithZeroQuantity)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.fieldErrors").isNotEmpty();
        }

        @Test
        @DisplayName("Should validate unit price - negative not allowed")
        void shouldValidateNegativeUnitPrice() {
            CreateOrderRequest requestWithNegativePrice = CreateOrderRequest.builder()
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .deliveryStreet("Rua das Flores")
                    .deliveryNumber("123")
                    .deliveryNeighborhood("Centro")
                    .deliveryCity("São Paulo")
                    .deliveryState("SP")
                    .deliveryZipCode("01234-567")
                    .items(List.of(
                            OrderItemRequest.builder()
                                    .productId(UUID.randomUUID())
                                    .productName("Pizza")
                                    .quantity(1)
                                    .unitPrice(new BigDecimal("-10.00"))
                                    .build()
                    ))
                    .build();

            webTestClient.post()
                    .uri("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestWithNegativePrice)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Should validate state format - must be 2 characters")
        void shouldValidateStateFormat() {
            CreateOrderRequest requestWithInvalidState = CreateOrderRequest.builder()
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .deliveryStreet("Rua das Flores")
                    .deliveryNumber("123")
                    .deliveryNeighborhood("Centro")
                    .deliveryCity("São Paulo")
                    .deliveryState("São Paulo")
                    .deliveryZipCode("01234-567")
                    .items(List.of(
                            OrderItemRequest.builder()
                                    .productId(UUID.randomUUID())
                                    .productName("Pizza")
                                    .quantity(1)
                                    .unitPrice(new BigDecimal("45.90"))
                                    .build()
                    ))
                    .build();

            webTestClient.post()
                    .uri("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestWithInvalidState)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.fieldErrors.deliveryState").isEqualTo("State must be 2 characters");
        }

        @Test
        @DisplayName("Should validate product name is required")
        void shouldValidateProductNameRequired() {
            CreateOrderRequest requestWithNoProductName = CreateOrderRequest.builder()
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .deliveryStreet("Rua das Flores")
                    .deliveryNumber("123")
                    .deliveryNeighborhood("Centro")
                    .deliveryCity("São Paulo")
                    .deliveryState("SP")
                    .deliveryZipCode("01234-567")
                    .items(List.of(
                            OrderItemRequest.builder()
                                    .productId(UUID.randomUUID())
                                    .quantity(1)
                                    .unitPrice(new BigDecimal("45.90"))
                                    .build()
                    ))
                    .build();

            webTestClient.post()
                    .uri("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestWithNoProductName)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle order with maximum items")
        void shouldHandleOrderWithMaximumItems() {
            List<OrderItemRequest> manyItems = java.util.stream.IntStream.range(0, 50)
                    .mapToObj(i -> OrderItemRequest.builder()
                            .productId(UUID.randomUUID())
                            .productName("Product " + i)
                            .quantity(1)
                            .unitPrice(new BigDecimal("10.00"))
                            .build())
                    .toList();

            CreateOrderRequest requestWithManyItems = CreateOrderRequest.builder()
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .deliveryStreet("Rua das Flores")
                    .deliveryNumber("123")
                    .deliveryNeighborhood("Centro")
                    .deliveryCity("São Paulo")
                    .deliveryState("SP")
                    .deliveryZipCode("01234-567")
                    .items(manyItems)
                    .build();

            OrderResponse responseWithManyItems = OrderResponse.builder()
                    .id(orderId)
                    .status(OrderStatus.PENDING)
                    .items(List.of())
                    .subtotal(new BigDecimal("500.00"))
                    .total(new BigDecimal("500.00"))
                    .build();

            when(orderService.createOrder(any(CreateOrderRequest.class)))
                    .thenReturn(Mono.just(responseWithManyItems));

            webTestClient.post()
                    .uri("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestWithManyItems)
                    .exchange()
                    .expectStatus().isCreated();
        }

        @Test
        @DisplayName("Should handle special characters in notes")
        void shouldHandleSpecialCharactersInNotes() {
            CreateOrderRequest requestWithSpecialChars = CreateOrderRequest.builder()
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .deliveryStreet("Rua das Flores")
                    .deliveryNumber("123")
                    .deliveryNeighborhood("Centro")
                    .deliveryCity("São Paulo")
                    .deliveryState("SP")
                    .deliveryZipCode("01234-567")
                    .notes("Observação com acentuação: não tocar campainha! @#$%")
                    .items(List.of(
                            OrderItemRequest.builder()
                                    .productId(UUID.randomUUID())
                                    .productName("Pizza Margherita")
                                    .quantity(1)
                                    .unitPrice(new BigDecimal("45.90"))
                                    .notes("Sem cebola, por favor!")
                                    .build()
                    ))
                    .build();

            when(orderService.createOrder(any(CreateOrderRequest.class)))
                    .thenReturn(Mono.just(sampleOrderResponse));

            webTestClient.post()
                    .uri("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestWithSpecialChars)
                    .exchange()
                    .expectStatus().isCreated();
        }

        @Test
        @DisplayName("Should handle high precision decimal values")
        void shouldHandleHighPrecisionDecimalValues() {
            CreateOrderRequest requestWithPrecision = CreateOrderRequest.builder()
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .deliveryStreet("Rua das Flores")
                    .deliveryNumber("123")
                    .deliveryNeighborhood("Centro")
                    .deliveryCity("São Paulo")
                    .deliveryState("SP")
                    .deliveryZipCode("01234-567")
                    .deliveryLatitude(new BigDecimal("-23.5505199999"))
                    .deliveryLongitude(new BigDecimal("-46.6333079999"))
                    .items(List.of(
                            OrderItemRequest.builder()
                                    .productId(UUID.randomUUID())
                                    .productName("Item")
                                    .quantity(1)
                                    .unitPrice(new BigDecimal("45.99"))
                                    .build()
                    ))
                    .build();

            when(orderService.createOrder(any(CreateOrderRequest.class)))
                    .thenReturn(Mono.just(sampleOrderResponse));

            webTestClient.post()
                    .uri("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestWithPrecision)
                    .exchange()
                    .expectStatus().isCreated();
        }
    }
}