package com.ecommerce.restaurant.infrastructure.messaging.producer;

import com.ecommerce.restaurant.domain.entity.Restaurant;
import com.ecommerce.restaurant.infrastructure.messaging.event.RestaurantEvent;
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
public class RestaurantEventProducer {

    private final KafkaTemplate<String, RestaurantEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.restaurant-events:restaurant-events}")
    private String restaurantEventsTopic;

    public void sendRestaurantCreated(Restaurant restaurant) {
        RestaurantEvent event = buildEvent(restaurant, RestaurantEvent.EventType.RESTAURANT_CREATED);
        sendEvent(event);
        log.info("Restaurant created event sent: {}", restaurant.getId());
    }

    public void sendRestaurantUpdated(Restaurant restaurant) {
        RestaurantEvent event = buildEvent(restaurant, RestaurantEvent.EventType.RESTAURANT_UPDATED);
        sendEvent(event);
        log.info("Restaurant updated event sent: {}", restaurant.getId());
    }

    public void sendRestaurantDeleted(UUID restaurantId, UUID ownerId) {
        RestaurantEvent event = RestaurantEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(RestaurantEvent.EventType.RESTAURANT_DELETED)
                .restaurantId(restaurantId)
                .ownerId(ownerId)
                .timestamp(LocalDateTime.now())
                .build();
        sendEvent(event);
        log.info("Restaurant deleted event sent: {}", restaurantId);
    }

    public void sendRestaurantOpened(Restaurant restaurant) {
        RestaurantEvent event = buildEvent(restaurant, RestaurantEvent.EventType.RESTAURANT_OPENED);
        sendEvent(event);
        log.info("Restaurant opened event sent: {}", restaurant.getId());
    }

    public void sendRestaurantClosed(Restaurant restaurant) {
        RestaurantEvent event = buildEvent(restaurant, RestaurantEvent.EventType.RESTAURANT_CLOSED);
        sendEvent(event);
        log.info("Restaurant closed event sent: {}", restaurant.getId());
    }

    public void sendRestaurantActivated(Restaurant restaurant) {
        RestaurantEvent event = buildEvent(restaurant, RestaurantEvent.EventType.RESTAURANT_ACTIVATED);
        sendEvent(event);
        log.info("Restaurant activated event sent: {}", restaurant.getId());
    }

    public void sendRestaurantSuspended(Restaurant restaurant) {
        RestaurantEvent event = buildEvent(restaurant, RestaurantEvent.EventType.RESTAURANT_SUSPENDED);
        sendEvent(event);
        log.info("Restaurant suspended event sent: {}", restaurant.getId());
    }

    public void sendOrdersPaused(Restaurant restaurant) {
        RestaurantEvent event = buildEvent(restaurant, RestaurantEvent.EventType.RESTAURANT_ORDERS_PAUSED);
        sendEvent(event);
        log.info("Restaurant orders paused event sent: {}", restaurant.getId());
    }

    public void sendOrdersResumed(Restaurant restaurant) {
        RestaurantEvent event = buildEvent(restaurant, RestaurantEvent.EventType.RESTAURANT_ORDERS_RESUMED);
        sendEvent(event);
        log.info("Restaurant orders resumed event sent: {}", restaurant.getId());
    }

    private RestaurantEvent buildEvent(Restaurant restaurant, RestaurantEvent.EventType eventType) {
        return RestaurantEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(eventType)
                .restaurantId(restaurant.getId())
                .ownerId(restaurant.getOwnerId())
                .name(restaurant.getName())
                .status(restaurant.getStatus() != null ? restaurant.getStatus().name() : null)
                .isOpen(restaurant.getIsOpen())
                .isAcceptingOrders(restaurant.getIsAcceptingOrders())
                .rating(restaurant.getRating())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void sendEvent(RestaurantEvent event) {
        kafkaTemplate.send(restaurantEventsTopic, event.getRestaurantId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send restaurant event: {}", ex.getMessage());
                    }
                });
    }
}