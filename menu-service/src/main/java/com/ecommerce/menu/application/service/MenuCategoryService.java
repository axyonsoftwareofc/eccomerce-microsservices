package com.ecommerce.menu.application.service;

import com.ecommerce.menu.application.dto.request.CreateCategoryRequest;
import com.ecommerce.menu.application.dto.response.MenuCategoryResponse;
import com.ecommerce.menu.application.mapper.MenuMapper;
import com.ecommerce.menu.domain.entity.MenuCategory;
import com.ecommerce.menu.domain.exception.CategoryNotFoundException;
import com.ecommerce.menu.infrastructure.repository.MenuCategoryRepository;
import com.ecommerce.menu.infrastructure.repository.MenuItemRepository;
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
public class MenuCategoryService {

    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemService menuItemService;
    private final MenuMapper menuMapper;

    @Transactional
    public Mono<MenuCategoryResponse> createCategory(CreateCategoryRequest request) {
        log.info("Creating category: {} for restaurant: {}", request.getName(), request.getRestaurantId());

        MenuCategory category = menuMapper.toCategoryEntity(request);
        category.setId(UUID.randomUUID());
        // isNew já é true por padrão no @Builder.Default

        return categoryRepository.save(category)
                .map(saved -> {
                    saved.markAsNotNew(); // Marca como não novo após salvar
                    return menuMapper.toCategoryResponseWithItems(saved, null, 0);
                })
                .doOnSuccess(c -> log.info("Category created: {}", c.getId()));
    }

    @Transactional(readOnly = true)
    public Mono<MenuCategoryResponse> getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)))
                .flatMap(this::enrichWithItemCount);
    }

    @Transactional(readOnly = true)
    public Flux<MenuCategoryResponse> getCategoriesByRestaurant(UUID restaurantId) {
        return categoryRepository.findByRestaurantIdOrderByDisplayOrder(restaurantId)
                .flatMap(this::enrichWithItemCount);
    }

    @Transactional(readOnly = true)
    public Flux<MenuCategoryResponse> getActiveCategoriesByRestaurant(UUID restaurantId) {
        return categoryRepository.findByRestaurantIdAndIsActiveTrueOrderByDisplayOrder(restaurantId)
                .filter(MenuCategory::isCurrentlyAvailable)
                .flatMap(this::enrichWithItemCount);
    }

    @Transactional(readOnly = true)
    public Mono<MenuCategoryResponse> getCategoryWithItems(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(categoryId)))
                .flatMap(category ->
                        menuItemService.getMenuItemsByCategory(categoryId)
                                .collectList()
                                .map(items -> menuMapper.toCategoryResponseWithItems(category, items, items.size()))
                );
    }

    @Transactional
    public Mono<MenuCategoryResponse> updateCategory(UUID id, CreateCategoryRequest request) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)))
                .flatMap(category -> {
                    if (request.getName() != null) category.setName(request.getName());
                    if (request.getDescription() != null) category.setDescription(request.getDescription());
                    if (request.getImageUrl() != null) category.setImageUrl(request.getImageUrl());
                    if (request.getDisplayOrder() != null) category.setDisplayOrder(request.getDisplayOrder());
                    if (request.getAvailableFrom() != null) category.setAvailableFrom(request.getAvailableFrom());
                    if (request.getAvailableUntil() != null) category.setAvailableUntil(request.getAvailableUntil());
                    return categoryRepository.save(category);
                })
                .flatMap(this::enrichWithItemCount);
    }

    @Transactional
    public Mono<MenuCategoryResponse> activateCategory(UUID id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)))
                .flatMap(category -> {
                    category.activate();
                    return categoryRepository.save(category);
                })
                .flatMap(this::enrichWithItemCount);
    }

    @Transactional
    public Mono<MenuCategoryResponse> deactivateCategory(UUID id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)))
                .flatMap(category -> {
                    category.deactivate();
                    return categoryRepository.save(category);
                })
                .flatMap(this::enrichWithItemCount);
    }

    @Transactional
    public Mono<Void> deleteCategory(UUID id) {
        log.info("Deleting category: {}", id);

        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)))
                .flatMap(category -> categoryRepository.deleteById(id));
    }

    private Mono<MenuCategoryResponse> enrichWithItemCount(MenuCategory category) {
        return menuItemRepository.countByCategoryId(category.getId())
                .map(count -> menuMapper.toCategoryResponseWithItems(category, null, count.intValue()));
    }
}