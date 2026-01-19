// infrastructure/repository/AddonRepository.java
package com.ecommerce.menu.infrastructure.repository;

import com.ecommerce.menu.domain.entity.MenuItemAddon;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AddonRepository extends R2dbcRepository<MenuItemAddon, UUID> {

    Flux<MenuItemAddon> findByMenuItemIdOrderByDisplayOrder(UUID menuItemId);

    Mono<Void> deleteByMenuItemId(UUID menuItemId);
}