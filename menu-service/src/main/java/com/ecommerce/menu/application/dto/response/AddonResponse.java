// application/dto/response/AddonResponse.java
package com.ecommerce.menu.application.dto.response;

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
public class AddonResponse {

    private UUID id;
    private UUID menuItemId;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean isAvailable;
    private Integer maxQuantity;
    private Boolean isRequired;
    private Integer displayOrder;
}