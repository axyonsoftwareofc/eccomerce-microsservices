package com.ecommerce.order.domain.entity;

public enum OrderStatus {
    PENDING,           // Pedido criado, aguardando confirmação
    CONFIRMED,         // Restaurante confirmou
    PREPARING,         // Em preparação
    READY,             // Pronto para retirada
    OUT_FOR_DELIVERY,  // Saiu para entrega
    DELIVERED,         // Entregue
    CANCELLED;         // Cancelado

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == PREPARING || newStatus == CANCELLED;
            case PREPARING -> newStatus == READY || newStatus == CANCELLED;
            case READY -> newStatus == OUT_FOR_DELIVERY || newStatus == CANCELLED;
            case OUT_FOR_DELIVERY -> newStatus == DELIVERED || newStatus == CANCELLED;
            case DELIVERED, CANCELLED -> false;
        };
    }

    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED;
    }

    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED;
    }
}