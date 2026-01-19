package com.ecommerce.restaurant.application.service;

import com.ecommerce.restaurant.application.dto.request.CreateRestaurantRequest;
import com.ecommerce.restaurant.application.dto.request.UpdateRestaurantRequest;
import com.ecommerce.restaurant.application.dto.response.RestaurantResponse;
import com.ecommerce.restaurant.application.mapper.RestaurantMapper;
import com.ecommerce.restaurant.domain.entity.Restaurant;
import com.ecommerce.restaurant.domain.entity.RestaurantStatus;
import com.ecommerce.restaurant.domain.exception.RestaurantNotFoundException;
import com.ecommerce.restaurant.infrastructure.messaging.producer.RestaurantEventProducer;
import com.ecommerce.restaurant.infrastructure.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantEventProducer eventProducer;

    @Transactional
    public Mono<RestaurantResponse> createRestaurant(CreateRestaurantRequest request) {
        log.info("Creating restaurant: {}", request.getName());

        Restaurant restaurant = restaurantMapper.toEntity(request);
        restaurant.setId(UUID.randomUUID());
        restaurant.setStatus(RestaurantStatus.PENDING_APPROVAL);

        return restaurantRepository.save(restaurant)
                .doOnSuccess(eventProducer::sendRestaurantCreated)
                .map(restaurantMapper::toResponse)
                .doOnSuccess(r -> log.info("Restaurant created: {}", r.getId()));
    }

    @Transactional(readOnly = true)
    public Mono<RestaurantResponse> getRestaurantById(UUID id) {
        return restaurantRepository.findById(id)
                .switchIfEmpty(Mono.error(new RestaurantNotFoundException(id)))
                .map(restaurantMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                .map(restaurantMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<RestaurantResponse> getActiveRestaurants() {
        return restaurantRepository.findByStatus(RestaurantStatus.ACTIVE)
                .map(restaurantMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<RestaurantResponse> getOpenRestaurants() {
        return restaurantRepository.findByStatusAndIsOpenTrue(RestaurantStatus.ACTIVE)
                .filter(Restaurant::isCurrentlyOpen)
                .map(restaurantMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<RestaurantResponse> getRestaurantsByCategory(UUID categoryId) {
        return restaurantRepository.findByCategoryIdAndStatus(categoryId, RestaurantStatus.ACTIVE)
                .map(restaurantMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<RestaurantResponse> getRestaurantsByOwner(UUID ownerId) {
        return restaurantRepository.findByOwnerId(ownerId)
                .map(restaurantMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<RestaurantResponse> searchRestaurants(String query) {
        return restaurantRepository.searchByName(query)
                .map(restaurantMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<RestaurantResponse> getNearbyRestaurants(BigDecimal latitude, BigDecimal longitude, BigDecimal radiusKm) {
        return restaurantRepository.findByStatus(RestaurantStatus.ACTIVE)
                .filter(r -> r.deliversToLocation(latitude, longitude))
                .map(restaurantMapper::toResponse);
    }

    @Transactional
    public Mono<RestaurantResponse> updateRestaurant(UUID id, UpdateRestaurantRequest request) {
        log.info("Updating restaurant: {}", id);

        return restaurantRepository.findById(id)
                .switchIfEmpty(Mono.error(new RestaurantNotFoundException(id)))
                .flatMap(restaurant -> {
                    updateFields(restaurant, request);
                    return restaurantRepository.save(restaurant);
                })
                .doOnSuccess(eventProducer::sendRestaurantUpdated)
                .map(restaurantMapper::toResponse);
    }

    @Transactional
    public Mono<RestaurantResponse> openRestaurant(UUID id) {
        log.info("Opening restaurant: {}", id);

        return restaurantRepository.findById(id)
                .switchIfEmpty(Mono.error(new RestaurantNotFoundException(id)))
                .flatMap(restaurant -> {
                    restaurant.open();
                    return restaurantRepository.save(restaurant);
                })
                .doOnSuccess(eventProducer::sendRestaurantOpened)
                .map(restaurantMapper::toResponse);
    }

    @Transactional
    public Mono<RestaurantResponse> closeRestaurant(UUID id) {
        log.info("Closing restaurant: {}", id);

        return restaurantRepository.findById(id)
                .switchIfEmpty(Mono.error(new RestaurantNotFoundException(id)))
                .flatMap(restaurant -> {
                    restaurant.close();
                    return restaurantRepository.save(restaurant);
                })
                .doOnSuccess(eventProducer::sendRestaurantClosed)
                .map(restaurantMapper::toResponse);
    }

    @Transactional
    public Mono<RestaurantResponse> activateRestaurant(UUID id) {
        log.info("Activating restaurant: {}", id);

        return restaurantRepository.findById(id)
                .switchIfEmpty(Mono.error(new RestaurantNotFoundException(id)))
                .flatMap(restaurant -> {
                    restaurant.activate();
                    return restaurantRepository.save(restaurant);
                })
                .doOnSuccess(eventProducer::sendRestaurantActivated)
                .map(restaurantMapper::toResponse);
    }

    @Transactional
    public Mono<RestaurantResponse> suspendRestaurant(UUID id) {
        log.info("Suspending restaurant: {}", id);

        return restaurantRepository.findById(id)
                .switchIfEmpty(Mono.error(new RestaurantNotFoundException(id)))
                .flatMap(restaurant -> {
                    restaurant.suspend();
                    return restaurantRepository.save(restaurant);
                })
                .doOnSuccess(eventProducer::sendRestaurantSuspended)
                .map(restaurantMapper::toResponse);
    }

    @Transactional
    public Mono<RestaurantResponse> pauseOrders(UUID id) {
        log.info("Pausing orders for restaurant: {}", id);

        return restaurantRepository.findById(id)
                .switchIfEmpty(Mono.error(new RestaurantNotFoundException(id)))
                .flatMap(restaurant -> {
                    restaurant.pauseOrders();
                    return restaurantRepository.save(restaurant);
                })
                .doOnSuccess(eventProducer::sendOrdersPaused)
                .map(restaurantMapper::toResponse);
    }

    @Transactional
    public Mono<RestaurantResponse> resumeOrders(UUID id) {
        log.info("Resuming orders for restaurant: {}", id);

        return restaurantRepository.findById(id)
                .switchIfEmpty(Mono.error(new RestaurantNotFoundException(id)))
                .flatMap(restaurant -> {
                    restaurant.resumeOrders();
                    return restaurantRepository.save(restaurant);
                })
                .doOnSuccess(eventProducer::sendOrdersResumed)
                .map(restaurantMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteRestaurant(UUID id) {
        log.info("Deleting restaurant: {}", id);

        return restaurantRepository.findById(id)
                .switchIfEmpty(Mono.error(new RestaurantNotFoundException(id)))
                .flatMap(restaurant ->
                        restaurantRepository.deleteById(id)
                                .doOnSuccess(v -> eventProducer.sendRestaurantDeleted(id, restaurant.getOwnerId()))
                );
    }

    private void updateFields(Restaurant restaurant, UpdateRestaurantRequest request) {
        if (request.getName() != null) restaurant.setName(request.getName());
        if (request.getDescription() != null) restaurant.setDescription(request.getDescription());
        if (request.getLogoUrl() != null) restaurant.setLogoUrl(request.getLogoUrl());
        if (request.getBannerUrl() != null) restaurant.setBannerUrl(request.getBannerUrl());
        if (request.getPhone() != null) restaurant.setPhone(request.getPhone());
        if (request.getEmail() != null) restaurant.setEmail(request.getEmail());
        if (request.getAddressStreet() != null) restaurant.setAddressStreet(request.getAddressStreet());
        if (request.getAddressNumber() != null) restaurant.setAddressNumber(request.getAddressNumber());
        if (request.getAddressComplement() != null) restaurant.setAddressComplement(request.getAddressComplement());
        if (request.getAddressNeighborhood() != null) restaurant.setAddressNeighborhood(request.getAddressNeighborhood());
        if (request.getAddressCity() != null) restaurant.setAddressCity(request.getAddressCity());
        if (request.getAddressState() != null) restaurant.setAddressState(request.getAddressState());
        if (request.getAddressZipCode() != null) restaurant.setAddressZipCode(request.getAddressZipCode());
        if (request.getLatitude() != null) restaurant.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) restaurant.setLongitude(request.getLongitude());
        if (request.getDeliveryRadiusKm() != null) restaurant.setDeliveryRadiusKm(request.getDeliveryRadiusKm());
        if (request.getMinOrderValue() != null) restaurant.setMinOrderValue(request.getMinOrderValue());
        if (request.getDeliveryFee() != null) restaurant.setDeliveryFee(request.getDeliveryFee());
        if (request.getAvgPreparationTime() != null) restaurant.setAvgPreparationTime(request.getAvgPreparationTime());
        if (request.getAvgDeliveryTime() != null) restaurant.setAvgDeliveryTime(request.getAvgDeliveryTime());
        if (request.getOpensAt() != null) restaurant.setOpensAt(request.getOpensAt());
        if (request.getClosesAt() != null) restaurant.setClosesAt(request.getClosesAt());
        if (request.getIsOpenOnWeekends() != null) restaurant.setIsOpenOnWeekends(request.getIsOpenOnWeekends());
        if (request.getCategoryId() != null) restaurant.setCategoryId(request.getCategoryId());
    }
}