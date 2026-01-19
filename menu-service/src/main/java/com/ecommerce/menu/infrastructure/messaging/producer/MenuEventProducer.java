package com.ecommerce.menu.infrastructure.messaging.producer;

import com.ecommerce.menu.domain.entity.MenuItem;
import com.ecommerce.menu.infrastructure.messaging.event.MenuItemEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuEventProducer {

    private final KafkaTemplate<String, MenuItemEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.menu-events:menu-events}")
    private String menuEventsTopic;

    public void sendMenuItemCreated(MenuItem item) {
        MenuItemEvent event = buildEvent(item, MenuItemEvent.EventType.MENU_ITEM_CREATED);
        sendEvent(event);
        log.info("Menu item created event sent: {}", item.getId());
    }

    public void sendMenuItemUpdated(MenuItem item) {
        MenuItemEvent event = buildEvent(item, MenuItemEvent.EventType.MENU_ITEM_UPDATED);
        sendEvent(event);
        log.info("Menu item updated event sent: {}", item.getId());
    }

    public void sendMenuItemDeleted(UUID menuItemId, UUID restaurantId) {
        MenuItemEvent event = MenuItemEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(MenuItemEvent.EventType.MENU_ITEM_DELETED)
                .menuItemId(menuItemId)
                .restaurantId(restaurantId)
                .timestamp(LocalDateTime.now())
                .build();
        sendEvent(event);
        log.info("Menu item deleted event sent: {}", menuItemId);
    }

    public void sendMenuItemAvailable(MenuItem item) {
        MenuItemEvent event = buildEvent(item, MenuItemEvent.EventType.MENU_ITEM_AVAILABLE);
        sendEvent(event);
        log.info("Menu item available event sent: {}", item.getId());
    }

    public void sendMenuItemUnavailable(MenuItem item) {
        MenuItemEvent event = buildEvent(item, MenuItemEvent.EventType.MENU_ITEM_UNAVAILABLE);
        sendEvent(event);
        log.info("Menu item unavailable event sent: {}", item.getId());
    }

    public void sendMenuItemFeatured(MenuItem item) {
        MenuItemEvent event = buildEvent(item, MenuItemEvent.EventType.MENU_ITEM_FEATURED);
        sendEvent(event);
        log.info("Menu item featured event sent: {}", item.getId());
    }

    public void sendMenuItemUnfeatured(MenuItem item) {
        MenuItemEvent event = buildEvent(item, MenuItemEvent.EventType.MENU_ITEM_UNFEATURED);
        sendEvent(event);
        log.info("Menu item unfeatured event sent: {}", item.getId());
    }

    private MenuItemEvent buildEvent(MenuItem item, MenuItemEvent.EventType eventType) {
        return MenuItemEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(eventType)
                .menuItemId(item.getId())
                .restaurantId(item.getRestaurantId())
                .name(item.getName())
                .price(item.getPrice())
                .isAvailable(item.getIsAvailable())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void sendEvent(MenuItemEvent event) {
        kafkaTemplate.send(menuEventsTopic, event.getMenuItemId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send menu event: {}", ex.getMessage());
                    }
                });
    }
}