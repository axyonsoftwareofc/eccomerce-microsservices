package com.ecommerce.order.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Table("order_items")
public class OrderItem {

    @Id
    private UUID id;

    @Column("order_id")
    private UUID orderId;

    @Column("product_id")
    private UUID productId;

    @Column("product_name")
    private String productName;

    @Column("quantity")
    private Integer quantity;

    @Column("unit_price")
    private BigDecimal unitPrice;

    @Column("total_price")
    private BigDecimal totalPrice;

    @Column("notes")
    private String notes;

    @Column("created_at")
    private LocalDateTime createdAt;

    // ========== MÉTODOS DE DOMÍNIO ==========

    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public static OrderItem create(UUID productId, String productName,
                                   Integer quantity, BigDecimal unitPrice, String notes) {
        OrderItem item = OrderItem.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .productName(productName)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .notes(notes)
                .createdAt(LocalDateTime.now())
                .build();
        item.calculateTotalPrice();
        return item;
    }
}