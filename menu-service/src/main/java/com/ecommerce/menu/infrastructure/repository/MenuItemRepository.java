// infrastructure/repository/MenuItemRepository.java
package com.ecommerce.menu.infrastructure.repository;

import com.ecommerce.menu.domain.entity.MenuItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface MenuItemRepository extends R2dbcRepository<MenuItem, UUID> {

    Flux<MenuItem> findByRestaurantIdOrderByDisplayOrder(UUID restaurantId);

    Flux<MenuItem> findByCategoryIdOrderByDisplayOrder(UUID categoryId);

    Flux<MenuItem> findByRestaurantIdAndIsAvailableTrue(UUID restaurantId);

    Flux<MenuItem> findByRestaurantIdAndIsFeaturedTrue(UUID restaurantId);

    Flux<MenuItem> findByRestaurantIdOrderByTotalOrdersDesc(UUID restaurantId);

    Mono<Long> countByCategoryId(UUID categoryId);

    @Query("SELECT * FROM menu_items WHERE restaurant_id = :restaurantId AND LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Flux<MenuItem> searchByName(UUID restaurantId, String name);
}