package com.ecommerce.menu.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("menu_item_variants")
public class MenuItemVariant {

    @Id
    private UUID id;

    @Column("menu_item_id")
    private UUID menuItemId;

    @Column("name")
    private String name; // "Pequeno", "Médio", "Grande", "Família"

    @Column("variant_type")
    private VariantType variantType; // SIZE, FLAVOR, PORTION

    @Column("price")
    private BigDecimal price; // Preço desta variante (substitui o preço base)

    @Column("price_modifier")
    private BigDecimal priceModifier; // OU usa modificador (+5.00, -2.00)

    @Column("serves")
    private Integer serves; // Quantas pessoas serve esta variante

    @Column("is_default")
    @Builder.Default
    private Boolean isDefault = false; // Se é a opção padrão

    @Column("is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column("display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    // ========== MÉTODOS DE DOMÍNIO ==========

    public BigDecimal calculateFinalPrice(BigDecimal basePrice) {
        if (this.price != null) {
            return this.price; // Usa preço fixo da variante
        }
        if (this.priceModifier != null) {
            return basePrice.add(this.priceModifier); // Usa modificador
        }
        return basePrice; // Usa preço base do item
    }

    public void markAsAvailable() {
        this.isAvailable = true;
    }

    public void markAsUnavailable() {
        this.isAvailable = false;
    }
}