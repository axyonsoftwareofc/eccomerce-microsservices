// infrastructure/repository/MenuCategoryRepository.java
package com.ecommerce.menu.infrastructure.repository;

import com.ecommerce.menu.domain.entity.MenuCategory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface MenuCategoryRepository extends R2dbcRepository<MenuCategory, UUID> {

    Flux<MenuCategory> findByRestaurantIdOrderByDisplayOrder(UUID restaurantId);

    Flux<MenuCategory> findByRestaurantIdAndIsActiveTrueOrderByDisplayOrder(UUID restaurantId);
}