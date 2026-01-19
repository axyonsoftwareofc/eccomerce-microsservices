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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("menu_items")
public class MenuItem implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column("restaurant_id")
    private UUID restaurantId;

    @Column("category_id")
    private UUID categoryId;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("image_url")
    private String imageUrl;

    @Column("price")
    private BigDecimal price;

    @Column("original_price")
    private BigDecimal originalPrice;

    @Column("discount_percentage")
    private BigDecimal discountPercentage;

    @Column("preparation_time")
    private Integer preparationTime;

    @Column("serves")
    private Integer serves;

    @Column("calories")
    private Integer calories;

    @Column("is_vegetarian")
    @Builder.Default
    private Boolean isVegetarian = false;

    @Column("is_vegan")
    @Builder.Default
    private Boolean isVegan = false;

    @Column("is_gluten_free")
    @Builder.Default
    private Boolean isGlutenFree = false;

    @Column("is_spicy")
    @Builder.Default
    private Boolean isSpicy = false;

    @Column("spicy_level")
    private Integer spicyLevel;

    @Column("is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column("is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column("is_best_seller")
    @Builder.Default
    private Boolean isBestSeller = false;

    @Column("available_from")
    private LocalTime availableFrom;

    @Column("available_until")
    private LocalTime availableUntil;

    @Column("stock_quantity")
    private Integer stockQuantity;

    @Column("max_quantity_per_order")
    private Integer maxQuantityPerOrder;

    @Column("display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column("total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

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

    public MenuItem markAsNotNew() {
        this.newEntity = false;
        return this;
    }

    // ========== MÉTODOS DE DOMÍNIO ==========

    public boolean isCurrentlyAvailable() {
        if (!Boolean.TRUE.equals(isAvailable)) {
            return false;
        }

        if (stockQuantity != null && stockQuantity <= 0) {
            return false;
        }

        if (availableFrom == null || availableUntil == null) {
            return true;
        }

        LocalTime now = LocalTime.now();
        return now.isAfter(availableFrom) && now.isBefore(availableUntil);
    }

    public void markAsAvailable() {
        this.isAvailable = true;
    }

    public void markAsUnavailable() {
        this.isAvailable = false;
    }

    public void feature() {
        this.isFeatured = true;
    }

    public void unfeature() {
        this.isFeatured = false;
    }

    public void decrementStock(int quantity) {
        if (this.stockQuantity != null) {
            this.stockQuantity = Math.max(0, this.stockQuantity - quantity);
            if (this.stockQuantity == 0) {
                this.isAvailable = false;
            }
        }
    }

    public void incrementStock(int quantity) {
        if (this.stockQuantity != null) {
            this.stockQuantity += quantity;
            if (this.stockQuantity > 0 && !Boolean.TRUE.equals(this.isAvailable)) {
                this.isAvailable = true;
            }
        }
    }

    public void incrementOrderCount() {
        this.totalOrders = (this.totalOrders == null ? 0 : this.totalOrders) + 1;
    }

    public BigDecimal getFinalPrice() {
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = price.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
            return price.subtract(discount);
        }
        return price;
    }

    public boolean hasDiscount() {
        return discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0;
    }
}