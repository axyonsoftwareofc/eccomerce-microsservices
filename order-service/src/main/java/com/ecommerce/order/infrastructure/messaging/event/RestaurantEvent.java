// order-service/src/main/java/com/ecommerce/order/infrastructure/messaging/event/RestaurantEvent.java
package com.ecommerce.order.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento recebido do Restaurant Service via Kafka.
 * Espelha a estrutura do evento enviado pelo producer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantEvent {

    private UUID eventId;
    private EventType eventType;
    private UUID restaurantId;
    private UUID ownerId;
    private String name;
    private String status;
    private Boolean isOpen;
    private Boolean isAcceptingOrders;
    private BigDecimal rating;
    private LocalDateTime timestamp;

    public enum EventType {
        RESTAURANT_CREATED,
        RESTAURANT_UPDATED,
        RESTAURANT_DELETED,
        RESTAURANT_OPENED,
        RESTAURANT_CLOSED,
        RESTAURANT_ACTIVATED,
        RESTAURANT_SUSPENDED,
        RESTAURANT_ORDERS_PAUSED,
        RESTAURANT_ORDERS_RESUMED
    }

    public boolean isRestaurantAvailable() {
        return Boolean.TRUE.equals(isOpen) && Boolean.TRUE.equals(isAcceptingOrders);
    }

    public boolean isRestaurantClosed() {
        return eventType == EventType.RESTAURANT_CLOSED ||
                eventType == EventType.RESTAURANT_SUSPENDED ||
                eventType == EventType.RESTAURANT_DELETED;
    }
}