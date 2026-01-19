// infrastructure/controller/MenuItemController.java
package com.ecommerce.menu.infrastructure.controller;

import com.ecommerce.menu.application.dto.request.CreateMenuItemRequest;
import com.ecommerce.menu.application.dto.request.UpdateMenuItemRequest;
import com.ecommerce.menu.application.dto.response.MenuItemResponse;
import com.ecommerce.menu.application.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menu-items")
@RequiredArgsConstructor
@Tag(name = "Menu Items", description = "Menu item management APIs")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new menu item")
    public Mono<MenuItemResponse> createMenuItem(@Valid @RequestBody CreateMenuItemRequest request) {
        return menuItemService.createMenuItem(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get menu item by ID")
    public Mono<MenuItemResponse> getMenuItem(@PathVariable UUID id) {
        return menuItemService.getMenuItemById(id);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get all menu items for a restaurant")
    public Flux<MenuItemResponse> getMenuItemsByRestaurant(@PathVariable UUID restaurantId) {
        return menuItemService.getMenuItemsByRestaurant(restaurantId);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get menu items by category")
    public Flux<MenuItemResponse> getMenuItemsByCategory(@PathVariable UUID categoryId) {
        return menuItemService.getMenuItemsByCategory(categoryId);
    }

    @GetMapping("/restaurant/{restaurantId}/available")
    @Operation(summary = "Get available menu items")
    public Flux<MenuItemResponse> getAvailableItems(@PathVariable UUID restaurantId) {
        return menuItemService.getAvailableItems(restaurantId);
    }

    @GetMapping("/restaurant/{restaurantId}/featured")
    @Operation(summary = "Get featured menu items")
    public Flux<MenuItemResponse> getFeaturedItems(@PathVariable UUID restaurantId) {
        return menuItemService.getFeaturedItems(restaurantId);
    }

    @GetMapping("/restaurant/{restaurantId}/best-sellers")
    @Operation(summary = "Get best selling items")
    public Flux<MenuItemResponse> getBestSellers(@PathVariable UUID restaurantId) {
        return menuItemService.getBestSellers(restaurantId);
    }

    @GetMapping("/restaurant/{restaurantId}/search")
    @Operation(summary = "Search menu items")
    public Flux<MenuItemResponse> searchItems(
            @PathVariable UUID restaurantId,
            @RequestParam String q) {
        return menuItemService.searchItems(restaurantId, q);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update menu item")
    public Mono<MenuItemResponse> updateMenuItem(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMenuItemRequest request) {
        return menuItemService.updateMenuItem(id, request);
    }

    @PatchMapping("/{id}/available")
    @Operation(summary = "Mark item as available")
    public Mono<MenuItemResponse> markAsAvailable(@PathVariable UUID id) {
        return menuItemService.markAsAvailable(id);
    }

    @PatchMapping("/{id}/unavailable")
    @Operation(summary = "Mark item as unavailable")
    public Mono<MenuItemResponse> markAsUnavailable(@PathVariable UUID id) {
        return menuItemService.markAsUnavailable(id);
    }

    @PatchMapping("/{id}/feature")
    @Operation(summary = "Feature item")
    public Mono<MenuItemResponse> featureItem(@PathVariable UUID id) {
        return menuItemService.featureItem(id);
    }

    @PatchMapping("/{id}/unfeature")
    @Operation(summary = "Unfeature item")
    public Mono<MenuItemResponse> unfeatureItem(@PathVariable UUID id) {
        return menuItemService.unfeatureItem(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete menu item")
    public Mono<Void> deleteMenuItem(@PathVariable UUID id) {
        return menuItemService.deleteMenuItem(id);
    }
}