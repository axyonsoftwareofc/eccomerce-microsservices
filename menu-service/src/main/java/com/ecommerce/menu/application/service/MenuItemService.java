package com.ecommerce.menu.application.service;

import com.ecommerce.menu.application.dto.request.CreateAddonRequest;
import com.ecommerce.menu.application.dto.request.CreateMenuItemRequest;
import com.ecommerce.menu.application.dto.request.CreateVariantRequest;
import com.ecommerce.menu.application.dto.request.UpdateMenuItemRequest;
import com.ecommerce.menu.application.dto.response.AddonResponse;
import com.ecommerce.menu.application.dto.response.MenuItemResponse;
import com.ecommerce.menu.application.dto.response.VariantResponse;
import com.ecommerce.menu.application.mapper.MenuMapper;
import com.ecommerce.menu.domain.entity.MenuItem;
import com.ecommerce.menu.domain.entity.MenuItemAddon;
import com.ecommerce.menu.domain.entity.MenuItemVariant;
import com.ecommerce.menu.domain.exception.MenuItemNotFoundException;
import com.ecommerce.menu.infrastructure.repository.AddonRepository;
import com.ecommerce.menu.infrastructure.repository.MenuCategoryRepository;
import com.ecommerce.menu.infrastructure.repository.MenuItemRepository;
import com.ecommerce.menu.infrastructure.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository categoryRepository;
    private final AddonRepository addonRepository;
    private final VariantRepository variantRepository;
    private final MenuMapper menuMapper;

    @Transactional
    public Mono<MenuItemResponse> createMenuItem(CreateMenuItemRequest request) {
        log.info("Creating menu item: {} for restaurant: {}", request.getName(), request.getRestaurantId());

        MenuItem item = menuMapper.toEntity(request);
        item.setId(UUID.randomUUID());

        return menuItemRepository.save(item)
                .flatMap(savedItem -> {
                    Mono<List<VariantResponse>> variantsMono = createVariants(request.getVariants(), savedItem);
                    Mono<List<AddonResponse>> addonsMono = createAddons(request.getAddons(), savedItem);

                    return Mono.zip(variantsMono, addonsMono)
                            .map(tuple -> menuMapper.toResponseWithDetails(
                                    savedItem,
                                    null,
                                    tuple.getT1(),
                                    tuple.getT2()
                            ));
                })
                .doOnSuccess(r -> log.info("Menu item created: {}", r.getId()));
    }

    private Mono<List<VariantResponse>> createVariants(List<CreateVariantRequest> variants, MenuItem savedItem) {
        if (variants == null || variants.isEmpty()) {
            return Mono.just(new ArrayList<>());
        }

        return Flux.fromIterable(variants)
                .flatMap(variantRequest -> {
                    MenuItemVariant variant = menuMapper.toVariantEntity(variantRequest, savedItem.getId());
                    variant.setId(UUID.randomUUID());
                    return variantRepository.save(variant);
                })
                .map(variant -> menuMapper.toVariantResponse(variant, savedItem.getPrice()))
                .collectList();
    }

    private Mono<List<AddonResponse>> createAddons(List<CreateAddonRequest> addons, MenuItem savedItem) {
        if (addons == null || addons.isEmpty()) {
            return Mono.just(new ArrayList<>());
        }

        return Flux.fromIterable(addons)
                .flatMap(addonRequest -> {
                    MenuItemAddon addon = menuMapper.toAddonEntity(addonRequest, savedItem.getId());
                    addon.setId(UUID.randomUUID());
                    return addonRepository.save(addon);
                })
                .map(menuMapper::toAddonResponse)
                .collectList();
    }

    @Transactional(readOnly = true)
    public Mono<MenuItemResponse> getMenuItemById(UUID id) {
        return menuItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuItemNotFoundException(id)))
                .flatMap(this::enrichWithDetails);
    }

    @Transactional(readOnly = true)
    public Flux<MenuItemResponse> getMenuItemsByRestaurant(UUID restaurantId) {
        return menuItemRepository.findByRestaurantIdOrderByDisplayOrder(restaurantId)
                .flatMap(this::enrichWithDetails);
    }

    @Transactional(readOnly = true)
    public Flux<MenuItemResponse> getMenuItemsByCategory(UUID categoryId) {
        return menuItemRepository.findByCategoryIdOrderByDisplayOrder(categoryId)
                .flatMap(this::enrichWithDetails);
    }

    @Transactional(readOnly = true)
    public Flux<MenuItemResponse> getAvailableItems(UUID restaurantId) {
        return menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId)
                .filter(MenuItem::isCurrentlyAvailable)
                .flatMap(this::enrichWithDetails);
    }

    @Transactional(readOnly = true)
    public Flux<MenuItemResponse> getFeaturedItems(UUID restaurantId) {
        return menuItemRepository.findByRestaurantIdAndIsFeaturedTrue(restaurantId)
                .flatMap(this::enrichWithDetails);
    }

    @Transactional(readOnly = true)
    public Flux<MenuItemResponse> getBestSellers(UUID restaurantId) {
        return menuItemRepository.findByRestaurantIdOrderByTotalOrdersDesc(restaurantId)
                .take(10)
                .flatMap(this::enrichWithDetails);
    }

    @Transactional(readOnly = true)
    public Flux<MenuItemResponse> searchItems(UUID restaurantId, String query) {
        return menuItemRepository.searchByName(restaurantId, query)
                .flatMap(this::enrichWithDetails);
    }

    @Transactional
    public Mono<MenuItemResponse> updateMenuItem(UUID id, UpdateMenuItemRequest request) {
        log.info("Updating menu item: {}", id);

        return menuItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuItemNotFoundException(id)))
                .flatMap(item -> {
                    updateFields(item, request);
                    return menuItemRepository.save(item);
                })
                .flatMap(this::enrichWithDetails);
    }

    @Transactional
    public Mono<MenuItemResponse> markAsAvailable(UUID id) {
        return menuItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuItemNotFoundException(id)))
                .flatMap(item -> {
                    item.markAsAvailable();
                    return menuItemRepository.save(item);
                })
                .flatMap(this::enrichWithDetails);
    }

    @Transactional
    public Mono<MenuItemResponse> markAsUnavailable(UUID id) {
        return menuItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuItemNotFoundException(id)))
                .flatMap(item -> {
                    item.markAsUnavailable();
                    return menuItemRepository.save(item);
                })
                .flatMap(this::enrichWithDetails);
    }

    @Transactional
    public Mono<MenuItemResponse> featureItem(UUID id) {
        return menuItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuItemNotFoundException(id)))
                .flatMap(item -> {
                    item.feature();
                    return menuItemRepository.save(item);
                })
                .flatMap(this::enrichWithDetails);
    }

    @Transactional
    public Mono<MenuItemResponse> unfeatureItem(UUID id) {
        return menuItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuItemNotFoundException(id)))
                .flatMap(item -> {
                    item.unfeature();
                    return menuItemRepository.save(item);
                })
                .flatMap(this::enrichWithDetails);
    }

    @Transactional
    public Mono<Void> deleteMenuItem(UUID id) {
        log.info("Deleting menu item: {}", id);

        return menuItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new MenuItemNotFoundException(id)))
                .flatMap(item ->
                        addonRepository.deleteByMenuItemId(id)
                                .then(variantRepository.deleteByMenuItemId(id))
                                .then(menuItemRepository.deleteById(id))
                );
    }

    private Mono<MenuItemResponse> enrichWithDetails(MenuItem item) {
        Mono<String> categoryNameMono = categoryRepository.findById(item.getCategoryId())
                .map(category -> category.getName())
                .defaultIfEmpty("");

        Mono<List<VariantResponse>> variantsMono = variantRepository
                .findByMenuItemIdOrderByDisplayOrder(item.getId())
                .map(variant -> menuMapper.toVariantResponse(variant, item.getPrice()))
                .collectList();

        Mono<List<AddonResponse>> addonsMono = addonRepository
                .findByMenuItemIdOrderByDisplayOrder(item.getId())
                .map(menuMapper::toAddonResponse)
                .collectList();

        return Mono.zip(categoryNameMono, variantsMono, addonsMono)
                .map(tuple -> menuMapper.toResponseWithDetails(
                        item,
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3()
                ));
    }

    private void updateFields(MenuItem item, UpdateMenuItemRequest request) {
        if (request.getCategoryId() != null) item.setCategoryId(request.getCategoryId());
        if (request.getName() != null) item.setName(request.getName());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getImageUrl() != null) item.setImageUrl(request.getImageUrl());
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getOriginalPrice() != null) item.setOriginalPrice(request.getOriginalPrice());
        if (request.getDiscountPercentage() != null) item.setDiscountPercentage(request.getDiscountPercentage());
        if (request.getPreparationTime() != null) item.setPreparationTime(request.getPreparationTime());
        if (request.getServes() != null) item.setServes(request.getServes());
        if (request.getCalories() != null) item.setCalories(request.getCalories());
        if (request.getIsVegetarian() != null) item.setIsVegetarian(request.getIsVegetarian());
        if (request.getIsVegan() != null) item.setIsVegan(request.getIsVegan());
        if (request.getIsGlutenFree() != null) item.setIsGlutenFree(request.getIsGlutenFree());
        if (request.getIsSpicy() != null) item.setIsSpicy(request.getIsSpicy());
        if (request.getSpicyLevel() != null) item.setSpicyLevel(request.getSpicyLevel());
        if (request.getIsAvailable() != null) item.setIsAvailable(request.getIsAvailable());
        if (request.getIsFeatured() != null) item.setIsFeatured(request.getIsFeatured());
        if (request.getIsBestSeller() != null) item.setIsBestSeller(request.getIsBestSeller());
        if (request.getAvailableFrom() != null) item.setAvailableFrom(request.getAvailableFrom());
        if (request.getAvailableUntil() != null) item.setAvailableUntil(request.getAvailableUntil());
        if (request.getStockQuantity() != null) item.setStockQuantity(request.getStockQuantity());
        if (request.getMaxQuantityPerOrder() != null) item.setMaxQuantityPerOrder(request.getMaxQuantityPerOrder());
        if (request.getDisplayOrder() != null) item.setDisplayOrder(request.getDisplayOrder());
    }
}