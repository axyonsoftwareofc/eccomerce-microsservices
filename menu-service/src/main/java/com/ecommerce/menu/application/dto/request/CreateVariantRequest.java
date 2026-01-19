// application/dto/request/CreateVariantRequest.java
package com.ecommerce.menu.application.dto.request;

import com.ecommerce.menu.domain.entity.VariantType;
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
public class CreateVariantRequest {

    private UUID menuItemId; // Pode ser null se vier dentro de CreateMenuItemRequest

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 30)
    private String name; // "P", "M", "G", "Família"

    @NotNull(message = "Variant type is required")
    private VariantType variantType;

    private BigDecimal price; // Preço fixo OU
    private BigDecimal priceModifier; // Modificador (+5.00)

    @Min(value = 1)
    private Integer serves;

    private Boolean isDefault;
    private Integer displayOrder;
}