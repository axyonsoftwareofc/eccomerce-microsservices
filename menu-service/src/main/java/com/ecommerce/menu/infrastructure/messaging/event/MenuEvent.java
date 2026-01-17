// infrastructure/messaging/event/ProductEvent.java
package com.ecommerce.menu.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuEvent {

    private UUID eventId;
    private EventType eventType;
    private UUID productId;
    private String name;
    private String sku;
    private BigDecimal price;
    private Boolean isActive;
    private LocalDateTime timestamp;

    public enum EventType {
        PRODUCT_CREATED,
        PRODUCT_UPDATED,
        PRODUCT_DELETED,
        PRODUCT_ACTIVATED,
        PRODUCT_DEACTIVATED
    }
}