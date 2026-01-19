package com.ecommerce.order.infrastructure.controller;

import com.ecommerce.order.application.dto.request.CreateOrderRequest;
import com.ecommerce.order.application.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.order.application.dto.response.OrderResponse;
import com.ecommerce.order.application.service.OrderService;
import com.ecommerce.order.domain.entity.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new order")
    public Mono<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public Mono<OrderResponse> getOrder(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get orders by customer")
    public Flux<OrderResponse> getOrdersByCustomer(@PathVariable UUID customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get orders by restaurant")
    public Flux<OrderResponse> getOrdersByRestaurant(@PathVariable UUID restaurantId) {
        return orderService.getOrdersByRestaurant(restaurantId);
    }

    @GetMapping("/restaurant/{restaurantId}/active")
    @Operation(summary = "Get active orders by restaurant")
    public Flux<OrderResponse> getActiveOrdersByRestaurant(@PathVariable UUID restaurantId) {
        return orderService.getActiveOrdersByRestaurant(restaurantId);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status")
    public Flux<OrderResponse> getOrdersByStatus(@PathVariable OrderStatus status) {
        return orderService.getOrdersByStatus(status);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public Mono<OrderResponse> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateOrderStatus(id, request);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order")
    public Mono<OrderResponse> cancelOrder(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        return orderService.cancelOrder(id, reason);
    }
}