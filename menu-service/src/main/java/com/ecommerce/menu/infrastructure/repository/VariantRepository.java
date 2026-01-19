// infrastructure/repository/VariantRepository.java
package com.ecommerce.menu.infrastructure.repository;

import com.ecommerce.menu.domain.entity.MenuItemVariant;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface VariantRepository extends R2dbcRepository<MenuItemVariant, UUID> {

    Flux<MenuItemVariant> findByMenuItemIdOrderByDisplayOrder(UUID menuItemId);

    Mono<Void> deleteByMenuItemId(UUID menuItemId);
}