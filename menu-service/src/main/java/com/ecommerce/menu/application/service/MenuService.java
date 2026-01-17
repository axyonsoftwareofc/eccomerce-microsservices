package com.ecommerce.menu.application.service;

import com.ecommerce.menu.application.dto.request.CreateMenuRequest;
import com.ecommerce.menu.application.dto.request.UpdateMenuRequest;
import com.ecommerce.menu.application.dto.response.MenuResponse;
import com.ecommerce.menu.application.mapper.MenuMapper;
import com.ecommerce.menu.domain.entity.Menu;
import com.ecommerce.menu.domain.exception.MenuAlreadyExistsException;
import com.ecommerce.menu.domain.exception.MenuNotFoundException;
import com.ecommerce.menu.infrastructure.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    @Transactional
    public Mono<MenuResponse> createMenu(CreateMenuRequest request) {
        log.info("Creating menu with SKU: {}", request.getSku());

        return menuRepository.existsBySku(request.getSku())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new MenuAlreadyExistsException(request.getSku()));
                    }

                    Menu menu = menuMapper.toEntity(request);
                    menu.setId(UUID.randomUUID());

                    return menuRepository.save(menu);
                })
                .map(menuMapper::toResponse)
                .doOnSuccess(p -> log.info("Menu created: {}", p.getId()))
                .doOnError(e -> log.error("Error creating menu: {}", e.getMessage()));
    }

    @Transactional(readOnly = true)
    public Mono<MenuResponse> getMenuById(UUID id) {
        log.debug("Fetching menu by id: {}", id);

        return menuRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuNotFoundException(id)))
                .map(menuMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<MenuResponse> getMenuBySku(String sku) {
        log.debug("Fetching menu by SKU: {}", sku);

        return menuRepository.findBySku(sku)
                .switchIfEmpty(Mono.error(new MenuNotFoundException(sku)))
                .map(menuMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<MenuResponse> getAllMenus() {
        log.debug("Fetching all menus");

        return menuRepository.findAll()
                .map(menuMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<MenuResponse> getMenusByCategory(UUID categoryId) {
        log.debug("Fetching menus by category: {}", categoryId);

        return menuRepository.findByCategoryId(categoryId)
                .map(menuMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<MenuResponse> getActiveMenus() {
        log.debug("Fetching active menus");

        return menuRepository.findByIsActiveTrue()
                .map(menuMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<MenuResponse> searchMenus(String query) {
        log.debug("Searching menus with query: {}", query);

        return menuRepository.searchByName(query)
                .map(menuMapper::toResponse);
    }

    @Transactional
    public Mono<MenuResponse> updateMenu(UUID id, UpdateMenuRequest request) {
        log.info("Updating menu: {}", id);

        return menuRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuNotFoundException(id)))
                .flatMap(menu -> {
                    updateFields(menu, request);
                    return menuRepository.save(menu);
                })
                .map(menuMapper::toResponse)
                .doOnSuccess(p -> log.info("Menu updated: {}", id));
    }

    @Transactional
    public Mono<Void> deleteMenu(UUID id) {
        log.info("Deleting menu: {}", id);

        return menuRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuNotFoundException(id)))
                .flatMap(menu -> menuRepository.deleteById(id))
                .doOnSuccess(v -> log.info("Menu deleted: {}", id));
    }

    @Transactional
    public Mono<MenuResponse> activateMenu(UUID id) {
        log.info("Activating menu: {}", id);

        return menuRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuNotFoundException(id)))
                .flatMap(menu -> {
                    menu.activate();
                    return menuRepository.save(menu);
                })
                .map(menuMapper::toResponse);
    }

    @Transactional
    public Mono<MenuResponse> deactivateMenu(UUID id) {
        log.info("Deactivating menu: {}", id);

        return menuRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuNotFoundException(id)))
                .flatMap(menu -> {
                    menu.deactivate();
                    return menuRepository.save(menu);
                })
                .map(menuMapper::toResponse);
    }

    private void updateFields(Menu menu, UpdateMenuRequest request) {
        if (request.getName() != null) {
            menu.setName(request.getName());
        }
        if (request.getDescription() != null) {
            menu.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            menu.setPrice(request.getPrice());
        }
        if (request.getCategoryId() != null) {
            menu.setCategoryId(request.getCategoryId());
        }
        if (request.getImageUrl() != null) {
            menu.setImageUrl(request.getImageUrl());
        }
        if (request.getIsActive() != null) {
            menu.setIsActive(request.getIsActive());
        }
    }
}