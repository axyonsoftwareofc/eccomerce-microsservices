// application/dto/request/CreateAddonRequest.java
package com.ecommerce.menu.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddonRequest {

    private UUID menuItemId; // Pode ser null se vier dentro de CreateMenuItemRequest

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50)
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00")
    private BigDecimal price;

    @Min(value = 1)
    private Integer maxQuantity;

    private Boolean isRequired;
    private Integer displayOrder;
}