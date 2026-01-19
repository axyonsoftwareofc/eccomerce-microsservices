package com.ecommerce.order.domain.entity;

import com.ecommerce.order.domain.exception.InvalidOrderStateException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column("customer_id")
    private UUID customerId;

    @Column("restaurant_id")
    private UUID restaurantId;

    @Column("status")
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    // ========== ENDEREÇO DE ENTREGA ==========
    @Column("delivery_street")
    private String deliveryStreet;

    @Column("delivery_number")
    private String deliveryNumber;

    @Column("delivery_complement")
    private String deliveryComplement;

    @Column("delivery_neighborhood")
    private String deliveryNeighborhood;

    @Column("delivery_city")
    private String deliveryCity;

    @Column("delivery_state")
    private String deliveryState;

    @Column("delivery_zip_code")
    private String deliveryZipCode;

    @Column("delivery_latitude")
    private BigDecimal deliveryLatitude;

    @Column("delivery_longitude")
    private BigDecimal deliveryLongitude;

    // ========== VALORES ==========
    @Column("subtotal")
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column("delivery_fee")
    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column("discount")
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column("total")
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    // ========== INFORMAÇÕES ADICIONAIS ==========
    @Column("notes")
    private String notes;

    @Column("cancellation_reason")
    private String cancellationReason;

    @Column("estimated_delivery_time")
    private Integer estimatedDeliveryTime;

    // ========== TIMESTAMPS DE STATUS ==========
    @Column("confirmed_at")
    private LocalDateTime confirmedAt;

    @Column("preparing_at")
    private LocalDateTime preparingAt;

    @Column("ready_at")
    private LocalDateTime readyAt;

    @Column("picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column("delivered_at")
    private LocalDateTime deliveredAt;

    @Column("cancelled_at")
    private LocalDateTime cancelledAt;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    // ========== RELACIONAMENTOS (Transient - não persiste diretamente) ==========
    @Transient
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    // ========== PERSISTABLE IMPLEMENTATION ==========
    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }

    public Order markAsNotNew() {
        this.isNew = false;
        return this;
    }

    // ========== MÉTODOS DE DOMÍNIO ==========

    public void confirm(Integer estimatedDeliveryTime) {
        validateTransition(OrderStatus.CONFIRMED);
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public void startPreparing() {
        validateTransition(OrderStatus.PREPARING);
        this.status = OrderStatus.PREPARING;
        this.preparingAt = LocalDateTime.now();
    }

    public void markAsReady() {
        validateTransition(OrderStatus.READY);
        this.status = OrderStatus.READY;
        this.readyAt = LocalDateTime.now();
    }

    public void startDelivery() {
        validateTransition(OrderStatus.OUT_FOR_DELIVERY);
        this.status = OrderStatus.OUT_FOR_DELIVERY;
        this.pickedUpAt = LocalDateTime.now();
    }

    public void complete() {
        validateTransition(OrderStatus.DELIVERED);
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        if (!status.canBeCancelled()) {
            throw new InvalidOrderStateException(
                    "Order cannot be cancelled in status: " + status
            );
        }
        this.status = OrderStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledAt = LocalDateTime.now();
    }

    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.total = subtotal
                .add(deliveryFee != null ? deliveryFee : BigDecimal.ZERO)
                .subtract(discount != null ? discount : BigDecimal.ZERO);
    }

    public void applyDiscount(BigDecimal discountAmount) {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    "Cannot apply discount to order in status: " + status
            );
        }
        if (discountAmount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount cannot be greater than subtotal");
        }
        this.discount = discountAmount;
        calculateTotals();
    }

    public String getFullDeliveryAddress() {
        StringBuilder sb = new StringBuilder();
        if (deliveryStreet != null) sb.append(deliveryStreet);
        if (deliveryNumber != null) sb.append(", ").append(deliveryNumber);
        if (deliveryComplement != null && !deliveryComplement.isBlank()) {
            sb.append(" - ").append(deliveryComplement);
        }
        if (deliveryNeighborhood != null) sb.append(", ").append(deliveryNeighborhood);
        if (deliveryCity != null) sb.append(", ").append(deliveryCity);
        if (deliveryState != null) sb.append(" - ").append(deliveryState);
        if (deliveryZipCode != null) sb.append(", ").append(deliveryZipCode);
        return sb.toString();
    }

    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }

    public boolean isActive() {
        return !status.isFinal();
    }

    private void validateTransition(OrderStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new InvalidOrderStateException(
                    String.format("Cannot transition from %s to %s", status, newStatus)
            );
        }
    }
}