package com.ecommerce.order.application.service;

import com.ecommerce.order.application.dto.request.CreateOrderRequest;
import com.ecommerce.order.application.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.order.application.dto.response.OrderResponse;
import com.ecommerce.order.application.mapper.OrderMapper;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderItem;
import com.ecommerce.order.domain.entity.OrderStatus;
import com.ecommerce.order.domain.exception.OrderNotFoundException;
import com.ecommerce.order.infrastructure.messaging.producer.OrderEventProducer;
import com.ecommerce.order.infrastructure.repository.OrderItemRepository;
import com.ecommerce.order.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderEventProducer eventProducer;

    @Transactional
    public Mono<OrderResponse> createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());

        Order order = orderMapper.toEntity(request);
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = orderMapper.toOrderItems(request.getItems(), order.getId());
        order.setItems(items);
        order.calculateTotals();

        return orderRepository.save(order)
                .flatMap(savedOrder -> saveOrderItems(items, savedOrder))
                .doOnSuccess(o -> {
                    eventProducer.sendOrderCreated(o);
                    log.info("Order created: {}", o.getId());
                })
                .map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<OrderResponse> getOrderById(UUID id) {
        return findOrderWithItems(id)
                .map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<OrderResponse> getOrdersByCustomer(UUID customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .flatMap(this::loadOrderItems)
                .map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<OrderResponse> getOrdersByRestaurant(UUID restaurantId) {
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .flatMap(this::loadOrderItems)
                .map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<OrderResponse> getActiveOrdersByRestaurant(UUID restaurantId) {
        return orderRepository.findActiveOrdersByRestaurant(restaurantId)
                .flatMap(this::loadOrderItems)
                .map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .flatMap(this::loadOrderItems)
                .map(orderMapper::toResponse);
    }

    @Transactional
    public Mono<OrderResponse> updateOrderStatus(UUID id, UpdateOrderStatusRequest request) {
        log.info("Updating order {} status to {}", id, request.getStatus());

        return findOrderWithItems(id)
                .flatMap(order -> {
                    OrderStatus previousStatus = order.getStatus();

                    switch (request.getStatus()) {
                        case CONFIRMED -> order.confirm(request.getEstimatedDeliveryTime());
                        case PREPARING -> order.startPreparing();
                        case READY -> order.markAsReady();
                        case OUT_FOR_DELIVERY -> order.startDelivery();
                        case DELIVERED -> order.complete();
                        case CANCELLED -> order.cancel(request.getCancellationReason());
                        default -> throw new IllegalArgumentException("Invalid status transition");
                    }

                    order.setUpdatedAt(LocalDateTime.now());

                    return orderRepository.save(order)
                            .doOnSuccess(o -> {
                                eventProducer.sendOrderStatusChanged(o, previousStatus);
                                log.info("Order {} status changed from {} to {}",
                                        id, previousStatus, o.getStatus());
                            });
                })
                .map(orderMapper::toResponse);
    }

    @Transactional
    public Mono<OrderResponse> cancelOrder(UUID id, String reason) {
        log.info("Cancelling order: {}", id);

        return findOrderWithItems(id)
                .flatMap(order -> {
                    OrderStatus previousStatus = order.getStatus();
                    order.cancel(reason);
                    order.setUpdatedAt(LocalDateTime.now());

                    return orderRepository.save(order)
                            .doOnSuccess(o -> {
                                eventProducer.sendOrderCancelled(o);
                                log.info("Order cancelled: {}", id);
                            });
                })
                .map(orderMapper::toResponse);
    }

    // ========== MÉTODOS PRIVADOS ==========

    private Mono<Order> findOrderWithItems(UUID id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(id)))
                .flatMap(this::loadOrderItems);
    }

    private Mono<Order> loadOrderItems(Order order) {
        return orderItemRepository.findByOrderId(order.getId())
                .collectList()
                .map(items -> {
                    order.setItems(items);
                    return order;
                });
    }

    private Mono<Order> saveOrderItems(List<OrderItem> items, Order order) {
        return Flux.fromIterable(items)
                .flatMap(orderItemRepository::save)
                .collectList()
                .map(savedItems -> {
                    order.setItems(savedItems);
                    return order;
                });
    }
}