// application/dto/request/CreateMenuItemRequest.java
package com.ecommerce.menu.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuItemRequest {

    @NotNull(message = "Restaurant ID is required")
    private UUID restaurantId;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    private String imageUrl;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal originalPrice;
    private BigDecimal discountPercentage;

    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    private Integer preparationTime;

    @Min(value = 1, message = "Serves must be at least 1")
    private Integer serves;

    @Min(value = 0, message = "Calories cannot be negative")
    private Integer calories;

    // Tags alimentares
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private Boolean isSpicy;

    @Min(value = 1) @Max(value = 3)
    private Integer spicyLevel;

    // Disponibilidade
    private Boolean isAvailable;
    private Boolean isFeatured;
    private LocalTime availableFrom;
    private LocalTime availableUntil;

    // Estoque
    private Integer stockQuantity;
    private Integer maxQuantityPerOrder;

    private Integer displayOrder;

    // Variantes e adicionais (opcional na criação)
    private List<CreateVariantRequest> variants;
    private List<CreateAddonRequest> addons;
}