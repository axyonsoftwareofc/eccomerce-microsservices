package com.ecommerce.order.application.dto.response;

import com.ecommerce.order.domain.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private UUID id;
    private UUID customerId;
    private UUID restaurantId;
    private OrderStatus status;

    // Endereço
    private String deliveryStreet;
    private String deliveryNumber;
    private String deliveryComplement;
    private String deliveryNeighborhood;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryZipCode;
    private String fullDeliveryAddress;
    private BigDecimal deliveryLatitude;
    private BigDecimal deliveryLongitude;

    // Valores
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal discount;
    private BigDecimal total;

    // Itens
    private List<OrderItemResponse> items;

    // Informações adicionais
    private String notes;
    private String cancellationReason;
    private Integer estimatedDeliveryTime;

    // Timestamps
    private LocalDateTime confirmedAt;
    private LocalDateTime preparingAt;
    private LocalDateTime readyAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}