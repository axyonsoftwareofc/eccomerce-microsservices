package com.ecommerce.order.infrastructure.repository;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, UUID> {

    Flux<Order> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    Flux<Order> findByRestaurantIdOrderByCreatedAtDesc(UUID restaurantId);

    Flux<Order> findByStatus(OrderStatus status);

    @Query("""
        SELECT * FROM orders 
        WHERE restaurant_id = :restaurantId 
        AND status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'READY')
        ORDER BY created_at ASC
    """)
    Flux<Order> findActiveOrdersByRestaurant(UUID restaurantId);

    @Query("""
        SELECT * FROM orders 
        WHERE customer_id = :customerId 
        AND status NOT IN ('DELIVERED', 'CANCELLED')
        ORDER BY created_at DESC
    """)
    Flux<Order> findActiveOrdersByCustomer(UUID customerId);
}