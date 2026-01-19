// domain/entity/Category.java
package com.ecommerce.restaurant.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("restaurant_categories")
public class Category implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column("name")
    private String name; // Pizzaria, Hamburgueria, Japonês, Italiana, etc.

    @Column("icon_url")
    private String iconUrl;

    @Column("is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column("display_order")
    private Integer displayOrder;

    // 3. Adicionar este bloco ANTES dos métodos de domínio:

    // ========== PERSISTABLE IMPLEMENTATION ==========
    @Transient
    @Builder.Default
    private boolean newEntity = true;

    @Override
    public boolean isNew() {
        return newEntity || id == null;
    }

    public Category markAsNotNew() {
        this.newEntity = false;
        return this;
    }
}