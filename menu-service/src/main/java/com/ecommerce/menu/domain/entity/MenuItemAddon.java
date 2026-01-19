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
@Table("menu_item_addons")
public class MenuItemAddon {

    @Id
    private UUID id;

    @Column("menu_item_id")
    private UUID menuItemId;

    @Column("name")
    private String name; // "Bacon extra", "Queijo cheddar", "Ovo"

    @Column("description")
    private String description;

    @Column("price")
    private BigDecimal price; // Preço adicional

    @Column("is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column("max_quantity")
    @Builder.Default
    private Integer maxQuantity = 1; // Máximo que pode adicionar

    @Column("is_required")
    @Builder.Default
    private Boolean isRequired = false; // Se é obrigatório escolher

    @Column("display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    // ========== MÉTODOS DE DOMÍNIO ==========

    public void markAsAvailable() {
        this.isAvailable = true;
    }

    public void markAsUnavailable() {
        this.isAvailable = false;
    }
}