// infrastructure/controller/RestaurantController.java
package com.ecommerce.restaurant.infrastructure.controller;

import com.ecommerce.restaurant.application.dto.request.CreateRestaurantRequest;
import com.ecommerce.restaurant.application.dto.request.UpdateRestaurantRequest;
import com.ecommerce.restaurant.application.dto.response.RestaurantResponse;
import com.ecommerce.restaurant.application.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Restaurant management APIs")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new restaurant")
    public Mono<RestaurantResponse> createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        return restaurantService.createRestaurant(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID")
    public Mono<RestaurantResponse> getRestaurant(@PathVariable UUID id) {
        return restaurantService.getRestaurantById(id);
    }

    @GetMapping
    @Operation(summary = "Get all restaurants")
    public Flux<RestaurantResponse> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/active")
    @Operation(summary = "Get active restaurants")
    public Flux<RestaurantResponse> getActiveRestaurants() {
        return restaurantService.getActiveRestaurants();
    }

    @GetMapping("/open")
    @Operation(summary = "Get open restaurants (currently accepting orders)")
    public Flux<RestaurantResponse> getOpenRestaurants() {
        return restaurantService.getOpenRestaurants();
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get restaurants by category")
    public Flux<RestaurantResponse> getRestaurantsByCategory(@PathVariable UUID categoryId) {
        return restaurantService.getRestaurantsByCategory(categoryId);
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get restaurants by owner")
    public Flux<RestaurantResponse> getRestaurantsByOwner(@PathVariable UUID ownerId) {
        return restaurantService.getRestaurantsByOwner(ownerId);
    }

    @GetMapping("/search")
    @Operation(summary = "Search restaurants by name")
    public Flux<RestaurantResponse> searchRestaurants(@RequestParam String q) {
        return restaurantService.searchRestaurants(q);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Get nearby restaurants that deliver to location")
    public Flux<RestaurantResponse> getNearbyRestaurants(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "5") BigDecimal radius) {
        return restaurantService.getNearbyRestaurants(lat, lng, radius);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant")
    public Mono<RestaurantResponse> updateRestaurant(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRestaurantRequest request) {
        return restaurantService.updateRestaurant(id, request);
    }

    @PatchMapping("/{id}/open")
    @Operation(summary = "Open restaurant")
    public Mono<RestaurantResponse> openRestaurant(@PathVariable UUID id) {
        return restaurantService.openRestaurant(id);
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Close restaurant")
    public Mono<RestaurantResponse> closeRestaurant(@PathVariable UUID id) {
        return restaurantService.closeRestaurant(id);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate restaurant (admin)")
    public Mono<RestaurantResponse> activateRestaurant(@PathVariable UUID id) {
        return restaurantService.activateRestaurant(id);
    }

    @PatchMapping("/{id}/suspend")
    @Operation(summary = "Suspend restaurant (admin)")
    public Mono<RestaurantResponse> suspendRestaurant(@PathVariable UUID id) {
        return restaurantService.suspendRestaurant(id);
    }

    @PatchMapping("/{id}/pause-orders")
    @Operation(summary = "Pause accepting orders")
    public Mono<RestaurantResponse> pauseOrders(@PathVariable UUID id) {
        return restaurantService.pauseOrders(id);
    }

    @PatchMapping("/{id}/resume-orders")
    @Operation(summary = "Resume accepting orders")
    public Mono<RestaurantResponse> resumeOrders(@PathVariable UUID id) {
        return restaurantService.resumeOrders(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete restaurant")
    public Mono<Void> deleteRestaurant(@PathVariable UUID id) {
        return restaurantService.deleteRestaurant(id);
    }
}