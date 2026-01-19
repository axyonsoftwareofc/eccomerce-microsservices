package com.ecommerce.order.infrastructure.messaging.producer;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderStatus;
import com.ecommerce.order.infrastructure.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreated(Order order) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "ORDER_CREATED");
        event.put("orderId", order.getId().toString());
        event.put("customerId", order.getCustomerId().toString());
        event.put("restaurantId", order.getRestaurantId().toString());
        event.put("total", order.getTotal());
        event.put("status", order.getStatus().name());
        event.put("timestamp", System.currentTimeMillis());

        sendEvent(order.getId().toString(), event);
        log.info("Order created event sent: {}", order.getId());
    }

    public void sendOrderStatusChanged(Order order, OrderStatus previousStatus) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "ORDER_STATUS_CHANGED");
        event.put("orderId", order.getId().toString());
        event.put("customerId", order.getCustomerId().toString());
        event.put("restaurantId", order.getRestaurantId().toString());
        event.put("previousStatus", previousStatus.name());
        event.put("newStatus", order.getStatus().name());
        event.put("timestamp", System.currentTimeMillis());

        sendEvent(order.getId().toString(), event);
        log.info("Order status changed event sent: {} -> {}", previousStatus, order.getStatus());
    }

    public void sendOrderCancelled(Order order) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "ORDER_CANCELLED");
        event.put("orderId", order.getId().toString());
        event.put("customerId", order.getCustomerId().toString());
        event.put("restaurantId", order.getRestaurantId().toString());
        event.put("reason", order.getCancellationReason());
        event.put("timestamp", System.currentTimeMillis());

        sendEvent(order.getId().toString(), event);
        log.info("Order cancelled event sent: {}", order.getId());
    }

    private void sendEvent(String key, Map<String, Object> event) {
        kafkaTemplate.send(KafkaConfig.ORDER_EVENTS_TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send event: {}", ex.getMessage());
                    }
                });
    }
}