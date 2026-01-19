// infrastructure/messaging/producer/RestaurantEventProducer.java
package com.ecommerce.restaurant.infrastructure.messaging.producer;

import com.ecommerce.restaurant.domain.entity.Restaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String RESTAURANT_EVENTS_TOPIC = "restaurant-events";

    public void sendRestaurantCreated(Restaurant restaurant) {
        var event = Map.of(
                "eventType", "RESTAURANT_CREATED",
                "restaurantId", restaurant.getId().toString(),
                "ownerId", restaurant.getOwnerId().toString(),
                "name", restaurant.getName(),
                "timestamp", System.currentTimeMillis()
        );

        kafkaTemplate.send(RESTAURANT_EVENTS_TOPIC, restaurant.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send restaurant created event: {}", ex.getMessage());
                    } else {
                        log.info("Restaurant created event sent: {}", restaurant.getId());
                    }
                });
    }

    public void sendRestaurantStatusChanged(Restaurant restaurant, String previousStatus) {
        var event = Map.of(
                "eventType", "RESTAURANT_STATUS_CHANGED",
                "restaurantId", restaurant.getId().toString(),
                "previousStatus", previousStatus,
                "newStatus", restaurant.getStatus().name(),
                "timestamp", System.currentTimeMillis()
        );

        kafkaTemplate.send(RESTAURANT_EVENTS_TOPIC, restaurant.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send status changed event: {}", ex.getMessage());
                    } else {
                        log.info("Restaurant status changed event sent: {}", restaurant.getId());
                    }
                });
    }

    public void sendRestaurantOpened(Restaurant restaurant) {
        var event = Map.of(
                "eventType", "RESTAURANT_OPENED",
                "restaurantId", restaurant.getId().toString(),
                "name", restaurant.getName(),
                "timestamp", System.currentTimeMillis()
        );

        kafkaTemplate.send(RESTAURANT_EVENTS_TOPIC, restaurant.getId().toString(), event);
        log.info("Restaurant opened event sent: {}", restaurant.getId());
    }

    public void sendRestaurantClosed(Restaurant restaurant) {
        var event = Map.of(
                "eventType", "RESTAURANT_CLOSED",
                "restaurantId", restaurant.getId().toString(),
                "name", restaurant.getName(),
                "timestamp", System.currentTimeMillis()
        );

        kafkaTemplate.send(RESTAURANT_EVENTS_TOPIC, restaurant.getId().toString(), event);
        log.info("Restaurant closed event sent: {}", restaurant.getId());
    }
}