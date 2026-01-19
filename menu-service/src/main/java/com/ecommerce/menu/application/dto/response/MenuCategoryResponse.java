// application/dto/response/MenuCategoryResponse.java
package com.ecommerce.menu.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryResponse {

    private UUID id;
    private UUID restaurantId;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean isActive;
    private Boolean isCurrentlyAvailable;
    private Integer displayOrder;
    private LocalTime availableFrom;
    private LocalTime availableUntil;
    private Integer itemCount;
    private List<MenuItemResponse> items; // Opcional
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}