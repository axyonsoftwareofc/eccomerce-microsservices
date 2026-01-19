package com.ecommerce.menu.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("menu_categories")
public class MenuCategory implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column("restaurant_id")
    private UUID restaurantId;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("image_url")
    private String imageUrl;

    @Column("is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column("display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column("available_from")
    private LocalTime availableFrom;

    @Column("available_until")
    private LocalTime availableUntil;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    // ========== PERSISTABLE IMPLEMENTATION ==========
    @Transient
    @Builder.Default
    private boolean newEntity = true;

    @Override
    public boolean isNew() {
        return newEntity || id == null;
    }

    public MenuCategory markAsNotNew() {
        this.newEntity = false;
        return this;
    }

    // ========== MÉTODOS DE DOMÍNIO ==========

    public boolean isCurrentlyAvailable() {
        if (!Boolean.TRUE.equals(isActive)) {
            return false;
        }

        if (availableFrom == null || availableUntil == null) {
            return true;
        }

        LocalTime now = LocalTime.now();
        return now.isAfter(availableFrom) && now.isBefore(availableUntil);
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}