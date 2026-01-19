// application/dto/response/VariantResponse.java
package com.ecommerce.menu.application.dto.response;

import com.ecommerce.menu.domain.entity.VariantType;
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
public class VariantResponse {

    private UUID id;
    private UUID menuItemId;
    private String name;
    private VariantType variantType;
    private BigDecimal price;
    private BigDecimal priceModifier;
    private BigDecimal finalPrice; // Calculado
    private Integer serves;
    private Boolean isDefault;
    private Boolean isAvailable;
    private Integer displayOrder;
}