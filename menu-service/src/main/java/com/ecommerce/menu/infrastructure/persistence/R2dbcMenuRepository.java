package com.ecommerce.menu.infrastructure.persistence;

import com.ecommerce.menu.domain.entity.Menu;
import com.ecommerce.menu.infrastructure.repository.MenuRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface R2dbcMenuRepository extends R2dbcRepository<Menu, UUID>, MenuRepository {

    Mono<Menu> findBySku(String sku);

    Flux<Menu> findByIsActiveTrue();

    Mono<Boolean> existsBySku(String sku);
}