// application/dto/response/MenuItemResponse.java
package com.ecommerce.menu.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {

    private UUID id;
    private UUID restaurantId;
    private UUID categoryId;
    private String categoryName;

    private String name;
    private String description;
    private String imageUrl;

    // Preços
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal finalPrice; // Preço com desconto
    private BigDecimal discountPercentage;
    private Boolean hasDiscount;

    // Informações
    private Integer preparationTime;
    private Integer serves;
    private Integer calories;

    // Tags
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private Boolean isSpicy;
    private Integer spicyLevel;

    // Status
    private Boolean isAvailable;
    private Boolean isCurrentlyAvailable;
    private Boolean isFeatured;
    private Boolean isBestSeller;

    // Horário
    private LocalTime availableFrom;
    private LocalTime availableUntil;

    // Estoque
    private Integer stockQuantity;
    private Integer maxQuantityPerOrder;

    private Integer displayOrder;
    private Integer totalOrders;

    // Variantes e Adicionais
    private List<VariantResponse> variants;
    private List<AddonResponse> addons;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}