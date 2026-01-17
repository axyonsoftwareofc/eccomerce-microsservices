// application/mapper/RestaurantMapper.java
package com.ecommerce.restaurant.application.mapper;

import com.ecommerce.restaurant.application.dto.request.CreateRestaurantRequest;
import com.ecommerce.restaurant.application.dto.response.RestaurantResponse;
import com.ecommerce.restaurant.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    public Restaurant toEntity(CreateRestaurantRequest request) {
        return Restaurant.builder()
                .ownerId(request.getOwnerId())
                .name(request.getName())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .bannerUrl(request.getBannerUrl())
                .phone(request.getPhone())
                .email(request.getEmail())
                .addressStreet(request.getAddressStreet())
                .addressNumber(request.getAddressNumber())
                .addressComplement(request.getAddressComplement())
                .addressNeighborhood(request.getAddressNeighborhood())
                .addressCity(request.getAddressCity())
                .addressState(request.getAddressState())
                .addressZipCode(request.getAddressZipCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .deliveryRadiusKm(request.getDeliveryRadiusKm())
                .minOrderValue(request.getMinOrderValue())
                .deliveryFee(request.getDeliveryFee())
                .avgPreparationTime(request.getAvgPreparationTime())
                .opensAt(request.getOpensAt())
                .closesAt(request.getClosesAt())
                .isOpenOnWeekends(request.getIsOpenOnWeekends())
                .categoryId(request.getCategoryId())
                .build();
    }

    public RestaurantResponse toResponse(Restaurant restaurant) {
        Integer estimatedDelivery = null;
        if (restaurant.getAvgPreparationTime() != null && restaurant.getAvgDeliveryTime() != null) {
            estimatedDelivery = restaurant.getAvgPreparationTime() + restaurant.getAvgDeliveryTime();
        }

        String fullAddress = buildFullAddress(restaurant);

        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .ownerId(restaurant.getOwnerId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .logoUrl(restaurant.getLogoUrl())
                .bannerUrl(restaurant.getBannerUrl())
                .phone(restaurant.getPhone())
                .email(restaurant.getEmail())
                .addressStreet(restaurant.getAddressStreet())
                .addressNumber(restaurant.getAddressNumber())
                .addressComplement(restaurant.getAddressComplement())
                .addressNeighborhood(restaurant.getAddressNeighborhood())
                .addressCity(restaurant.getAddressCity())
                .addressState(restaurant.getAddressState())
                .addressZipCode(restaurant.getAddressZipCode())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .fullAddress(fullAddress)
                .deliveryRadiusKm(restaurant.getDeliveryRadiusKm())
                .minOrderValue(restaurant.getMinOrderValue())
                .deliveryFee(restaurant.getDeliveryFee())
                .avgPreparationTime(restaurant.getAvgPreparationTime())
                .avgDeliveryTime(restaurant.getAvgDeliveryTime())
                .estimatedDeliveryTime(estimatedDelivery)
                .opensAt(restaurant.getOpensAt())
                .closesAt(restaurant.getClosesAt())
                .isOpenOnWeekends(restaurant.getIsOpenOnWeekends())
                .status(restaurant.getStatus())
                .isOpen(restaurant.getIsOpen())
                .isCurrentlyOpen(restaurant.isCurrentlyOpen())
                .isAcceptingOrders(restaurant.getIsAcceptingOrders())
                .rating(restaurant.getRating())
                .totalReviews(restaurant.getTotalReviews())
                .totalOrders(restaurant.getTotalOrders())
                .categoryId(restaurant.getCategoryId())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .build();
    }

    private String buildFullAddress(Restaurant r) {
        StringBuilder sb = new StringBuilder();
        if (r.getAddressStreet() != null) sb.append(r.getAddressStreet());
        if (r.getAddressNumber() != null) sb.append(", ").append(r.getAddressNumber());
        if (r.getAddressComplement() != null) sb.append(" - ").append(r.getAddressComplement());
        if (r.getAddressNeighborhood() != null) sb.append(", ").append(r.getAddressNeighborhood());
        if (r.getAddressCity() != null) sb.append(", ").append(r.getAddressCity());
        if (r.getAddressState() != null) sb.append(" - ").append(r.getAddressState());
        if (r.getAddressZipCode() != null) sb.append(", ").append(r.getAddressZipCode());
        return sb.toString();
    }
}