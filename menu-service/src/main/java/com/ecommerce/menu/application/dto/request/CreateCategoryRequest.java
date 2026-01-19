// application/dto/request/CreateCategoryRequest.java
package com.ecommerce.menu.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {

    @NotNull(message = "Restaurant ID is required")
    private UUID restaurantId;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(max = 200)
    private String description;

    private String imageUrl;

    private Integer displayOrder;

    private LocalTime availableFrom;
    private LocalTime availableUntil;
}