// application/dto/request/UpdateRestaurantRequest.java
package com.ecommerce.restaurant.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRestaurantRequest {

    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private String logoUrl;
    private String bannerUrl;
    private String phone;

    @Email
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

    // Configurações de entrega
    private BigDecimal deliveryRadiusKm;
    private BigDecimal minOrderValue;
    private BigDecimal deliveryFee;
    private Integer avgPreparationTime;
    private Integer avgDeliveryTime;

    // Horário
    private LocalTime opensAt;
    private LocalTime closesAt;
    private Boolean isOpenOnWeekends;

    // Categoria
    private UUID categoryId;
}