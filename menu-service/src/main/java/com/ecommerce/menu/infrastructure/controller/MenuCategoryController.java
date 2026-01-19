// infrastructure/controller/MenuCategoryController.java
package com.ecommerce.menu.infrastructure.controller;

import com.ecommerce.menu.application.dto.request.CreateCategoryRequest;
import com.ecommerce.menu.application.dto.response.MenuCategoryResponse;
import com.ecommerce.menu.application.service.MenuCategoryService;
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
@RequestMapping("/api/v1/menu-categories")
@RequiredArgsConstructor
@Tag(name = "Menu Categories", description = "Menu category management APIs")
public class MenuCategoryController {

    private final MenuCategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new category")
    public Mono<MenuCategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public Mono<MenuCategoryResponse> getCategory(@PathVariable UUID id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/{id}/with-items")
    @Operation(summary = "Get category with all items")
    public Mono<MenuCategoryResponse> getCategoryWithItems(@PathVariable UUID id) {
        return categoryService.getCategoryWithItems(id);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get all categories for a restaurant")
    public Flux<MenuCategoryResponse> getCategoriesByRestaurant(@PathVariable UUID restaurantId) {
        return categoryService.getCategoriesByRestaurant(restaurantId);
    }

    @GetMapping("/restaurant/{restaurantId}/active")
    @Operation(summary = "Get active categories for a restaurant")
    public Flux<MenuCategoryResponse> getActiveCategoriesByRestaurant(@PathVariable UUID restaurantId) {
        return categoryService.getActiveCategoriesByRestaurant(restaurantId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public Mono<MenuCategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate category")
    public Mono<MenuCategoryResponse> activateCategory(@PathVariable UUID id) {
        return categoryService.activateCategory(id);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate category")
    public Mono<MenuCategoryResponse> deactivateCategory(@PathVariable UUID id) {
        return categoryService.deactivateCategory(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category")
    public Mono<Void> deleteCategory(@PathVariable UUID id) {
        return categoryService.deleteCategory(id);
    }
}