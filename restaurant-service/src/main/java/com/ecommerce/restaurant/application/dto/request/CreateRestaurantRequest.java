// application/dto/request/CreateRestaurantRequest.java
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
public class CreateRestaurantRequest {

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    private String logoUrl;
    private String bannerUrl;

    @NotBlank(message = "Phone is required")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    // Endereço
    @NotBlank(message = "Street is required")
    private String addressStreet;

    @NotBlank(message = "Number is required")
    private String addressNumber;

    private String addressComplement;

    @NotBlank(message = "Neighborhood is required")
    private String addressNeighborhood;

    @NotBlank(message = "City is required")
    private String addressCity;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 2, message = "State must be 2 characters")
    private String addressState;

    @NotBlank(message = "ZIP code is required")
    private String addressZipCode;

    private BigDecimal latitude;
    private BigDecimal longitude;

    // Configurações de entrega
    @DecimalMin(value = "0.1", message = "Delivery radius must be at least 0.1 km")
    private BigDecimal deliveryRadiusKm;

    @DecimalMin(value = "0", message = "Minimum order value cannot be negative")
    private BigDecimal minOrderValue;

    @DecimalMin(value = "0", message = "Delivery fee cannot be negative")
    private BigDecimal deliveryFee;

    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    private Integer avgPreparationTime;

    // Horário de funcionamento
    private LocalTime opensAt;
    private LocalTime closesAt;
    private Boolean isOpenOnWeekends;

    // Categoria
    private UUID categoryId;
}