// domain/entity/RestaurantStatus.java
package com.ecommerce.restaurant.domain.entity;

public enum RestaurantStatus {
    PENDING_APPROVAL,  // Aguardando aprovação
    ACTIVE,            // Ativo e funcionando
    SUSPENDED,         // Suspenso (problemas)
    INACTIVE           // Inativo (fechado permanentemente)
}