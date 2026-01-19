// infrastructure/messaging/event/MenuItemEvent.java
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
public class MenuItemEvent {

    private UUID eventId;
    private EventType eventType;
    private UUID menuItemId;
    private UUID restaurantId;
    private String name;
    private BigDecimal price;
    private Boolean isAvailable;
    private LocalDateTime timestamp;

    public enum EventType {
        MENU_ITEM_CREATED,
        MENU_ITEM_UPDATED,
        MENU_ITEM_DELETED,
        MENU_ITEM_AVAILABLE,
        MENU_ITEM_UNAVAILABLE,
        MENU_ITEM_FEATURED,
        MENU_ITEM_UNFEATURED
    }
}