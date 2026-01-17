package com.ecommerce.menu.infrastructure.controller;

import com.ecommerce.menu.application.dto.request.CreateMenuRequest;
import com.ecommerce.menu.application.dto.request.UpdateMenuRequest;
import com.ecommerce.menu.application.dto.response.MenuResponse;
import com.ecommerce.menu.application.service.MenuService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Menus", description = "Menu management APIs")
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new menu")
    public Mono<MenuResponse> createMenu(@Valid @RequestBody CreateMenuRequest request) {
        return menuService.createMenu(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get menu by ID")
    public Mono<MenuResponse> getMenu(@PathVariable UUID id) {
        return menuService.getMenuById(id);
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get menu by SKU")
    public Mono<MenuResponse> getMenuBySku(@PathVariable String sku) {
        return menuService.getMenuBySku(sku);
    }

    @GetMapping
    @Operation(summary = "Get all menus")
    public Flux<MenuResponse> getAllMenus() {
        return menuService.getAllMenus();
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get menus by category")
    public Flux<MenuResponse> getMenusByCategory(@PathVariable UUID categoryId) {
        return menuService.getMenusByCategory(categoryId);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active menus only")
    public Flux<MenuResponse> getActiveMenus() {
        return menuService.getActiveMenus();
    }

    @GetMapping("/search")
    @Operation(summary = "Search menus by name")
    public Flux<MenuResponse> searchMenus(@RequestParam String q) {
        return menuService.searchMenus(q);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update menu")
    public Mono<MenuResponse> updateMenu(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMenuRequest request) {
        return menuService.updateMenu(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete menu")
    public Mono<Void> deleteMenu(@PathVariable UUID id) {
        return menuService.deleteMenu(id);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate menu")
    public Mono<MenuResponse> activateMenu(@PathVariable UUID id) {
        return menuService.activateMenu(id);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate menu")
    public Mono<MenuResponse> deactivateMenu(@PathVariable UUID id) {
        return menuService.deactivateMenu(id);
    }
}