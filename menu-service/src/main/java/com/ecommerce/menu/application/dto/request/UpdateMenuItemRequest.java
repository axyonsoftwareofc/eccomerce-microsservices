// application/dto/request/UpdateMenuItemRequest.java
package com.ecommerce.menu.application.dto.request;

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
public class UpdateMenuItemRequest {

    private UUID categoryId;

    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private String imageUrl;

    @DecimalMin(value = "0.01")
    private BigDecimal price;

    private BigDecimal originalPrice;
    private BigDecimal discountPercentage;

    @Min(value = 1)
    private Integer preparationTime;

    @Min(value = 1)
    private Integer serves;

    @Min(value = 0)
    private Integer calories;

    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private Boolean isSpicy;

    @Min(value = 1) @Max(value = 3)
    private Integer spicyLevel;

    private Boolean isAvailable;
    private Boolean isFeatured;
    private Boolean isBestSeller;
    private LocalTime availableFrom;
    private LocalTime availableUntil;

    private Integer stockQuantity;
    private Integer maxQuantityPerOrder;
    private Integer displayOrder;
}