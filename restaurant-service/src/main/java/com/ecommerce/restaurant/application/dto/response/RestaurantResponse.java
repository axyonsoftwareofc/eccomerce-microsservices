// application/dto/response/RestaurantResponse.java
package com.ecommerce.restaurant.application.dto.response;

import com.ecommerce.restaurant.domain.entity.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {

    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private String logoUrl;
    private String bannerUrl;
    private String phone;
    private String email;

    // Endereço
    private String addressStreet;
    private String addressNumber;
    private String addressComplement;
    private String addressNeighborhood;
    private String addressCity;
    private String addressState;
    private String addressZipCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String fullAddress;

    // Configurações de entrega
    private BigDecimal deliveryRadiusKm;
    private BigDecimal minOrderValue;
    private BigDecimal deliveryFee;
    private Integer avgPreparationTime;
    private Integer avgDeliveryTime;
    private Integer estimatedDeliveryTime; // preparo + entrega

    // Horário
    private LocalTime opensAt;
    private LocalTime closesAt;
    private Boolean isOpenOnWeekends;

    // Status
    private RestaurantStatus status;
    private Boolean isOpen;
    private Boolean isCurrentlyOpen; // Calculado
    private Boolean isAcceptingOrders;

    // Avaliação
    private BigDecimal rating;
    private Integer totalReviews;
    private Integer totalOrders;

    // Categoria
    private UUID categoryId;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}