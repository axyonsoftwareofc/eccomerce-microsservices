package com.ecommerce.restaurant.domain.entity;

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
@Table("restaurants")
public class Restaurant implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column("owner_id")
    private UUID ownerId;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("logo_url")
    private String logoUrl;

    @Column("banner_url")
    private String bannerUrl;

    @Column("phone")
    private String phone;

    @Column("email")
    private String email;

    // ========== ENDEREÇO ==========
    @Column("address_street")
    private String addressStreet;

    @Column("address_number")
    private String addressNumber;

    @Column("address_complement")
    private String addressComplement;

    @Column("address_neighborhood")
    private String addressNeighborhood;

    @Column("address_city")
    private String addressCity;

    @Column("address_state")
    private String addressState;

    @Column("address_zip_code")
    private String addressZipCode;

    @Column("latitude")
    private BigDecimal latitude;

    @Column("longitude")
    private BigDecimal longitude;

    // ========== CONFIGURAÇÕES DE ENTREGA ==========
    @Column("delivery_radius_km")
    private BigDecimal deliveryRadiusKm;

    @Column("min_order_value")
    private BigDecimal minOrderValue;

    @Column("delivery_fee")
    private BigDecimal deliveryFee;

    @Column("avg_preparation_time")
    private Integer avgPreparationTime;

    @Column("avg_delivery_time")
    private Integer avgDeliveryTime;

    // ========== HORÁRIO DE FUNCIONAMENTO ==========
    @Column("opens_at")
    private LocalTime opensAt;

    @Column("closes_at")
    private LocalTime closesAt;

    @Column("is_open_on_weekends")
    @Builder.Default
    private Boolean isOpenOnWeekends = true;

    // ========== CATEGORIA ==========
    @Column("category_id")
    private UUID categoryId;

    // ========== STATUS ==========
    @Column("status")
    @Builder.Default
    private RestaurantStatus status = RestaurantStatus.PENDING_APPROVAL;

    @Column("is_open")
    @Builder.Default
    private Boolean isOpen = false;

    @Column("is_accepting_orders")
    @Builder.Default
    private Boolean isAcceptingOrders = true;

    // ========== AVALIAÇÃO ==========
    @Column("rating")
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column("total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;

    @Column("total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

    // ========== TIMESTAMPS ==========
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

    public Restaurant markAsNotNew() {
        this.newEntity = false;
        return this;
    }

    // ========== MÉTODOS DE DOMÍNIO ==========

    public boolean isCurrentlyOpen() {
        if (!RestaurantStatus.ACTIVE.equals(this.status)) {
            return false;
        }

        if (!Boolean.TRUE.equals(this.isOpen)) {
            return false;
        }

        if (opensAt == null || closesAt == null) {
            return true;
        }

        LocalTime now = LocalTime.now();

        if (closesAt.isBefore(opensAt)) {
            return now.isAfter(opensAt) || now.isBefore(closesAt);
        }

        return now.isAfter(opensAt) && now.isBefore(closesAt);
    }

    public void open() {
        this.isOpen = true;
    }

    public void close() {
        this.isOpen = false;
    }

    public void activate() {
        this.status = RestaurantStatus.ACTIVE;
    }

    public void suspend() {
        this.status = RestaurantStatus.SUSPENDED;
    }

    public void pauseOrders() {
        this.isAcceptingOrders = false;
    }

    public void resumeOrders() {
        this.isAcceptingOrders = true;
    }

    public void updateRating(BigDecimal newRating, int newTotalReviews) {
        this.rating = newRating;
        this.totalReviews = newTotalReviews;
    }

    public void incrementOrderCount() {
        this.totalOrders = (this.totalOrders == null ? 0 : this.totalOrders) + 1;
    }

    public boolean canAcceptOrder() {
        return RestaurantStatus.ACTIVE.equals(this.status)
                && Boolean.TRUE.equals(this.isOpen)
                && Boolean.TRUE.equals(this.isAcceptingOrders);
    }

    public boolean deliversToLocation(BigDecimal customerLat, BigDecimal customerLng) {
        if (deliveryRadiusKm == null || latitude == null || longitude == null) {
            return true;
        }

        double distance = calculateDistance(
                latitude.doubleValue(), longitude.doubleValue(),
                customerLat.doubleValue(), customerLng.doubleValue()
        );

        return distance <= deliveryRadiusKm.doubleValue();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}