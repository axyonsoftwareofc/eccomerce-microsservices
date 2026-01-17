// infrastructure/repository/ProductRepository.java
package com.ecommerce.menu.infrastructure.repository;

import com.ecommerce.menu.domain.entity.Menu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface MenuRepository extends R2dbcRepository<Menu, UUID> {

    Mono<Menu> findBySku(String sku);

    Flux<Menu> findByCategoryId(UUID categoryId);

    Flux<Menu> findByIsActiveTrue();

    Mono<Boolean> existsBySku(String sku);

    @Query("SELECT * FROM products WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Flux<Menu> searchByName(String name);
}