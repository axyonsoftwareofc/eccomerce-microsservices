// infrastructure/repository/RestaurantRepository.java
package com.ecommerce.restaurant.infrastructure.repository;

import com.ecommerce.restaurant.domain.entity.Restaurant;
import com.ecommerce.restaurant.domain.entity.RestaurantStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface RestaurantRepository extends R2dbcRepository<Restaurant, UUID> {

    Flux<Restaurant> findByOwnerId(UUID ownerId);

    Flux<Restaurant> findByStatus(RestaurantStatus status);

    Flux<Restaurant> findByStatusAndIsOpenTrue(RestaurantStatus status);

    Flux<Restaurant> findByCategoryIdAndStatus(UUID categoryId, RestaurantStatus status);

    @Query("SELECT * FROM restaurants WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%')) AND status = 'ACTIVE'")
    Flux<Restaurant> searchByName(String name);

    @Query("SELECT * FROM restaurants WHERE status = 'ACTIVE' AND is_open = true AND is_accepting_orders = true")
    Flux<Restaurant> findOpenAndAcceptingOrders();
}