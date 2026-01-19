// infrastructure/repository/CategoryRepository.java
package com.ecommerce.restaurant.infrastructure.repository;

import com.ecommerce.restaurant.domain.entity.Category;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface CategoryRepository extends R2dbcRepository<Category, UUID> {

    Flux<Category> findByIsActiveTrueOrderByDisplayOrderAsc();
}