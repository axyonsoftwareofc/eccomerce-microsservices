// order-service/src/main/java/com/ecommerce/order/infrastructure/messaging/consumer/RestaurantEventConsumer.java
package com.ecommerce.order.infrastructure.messaging.consumer;

import com.ecommerce.order.domain.entity.OrderStatus;
import com.ecommerce.order.infrastructure.messaging.event.RestaurantEvent;
import com.ecommerce.order.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantEventConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "${spring.kafka.topics.restaurant-events:restaurant-events}",
            groupId = "${spring.kafka.consumer.group-id:order-service-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleRestaurantEvent(RestaurantEvent event) {
        log.info("Received restaurant event: {} for restaurant: {}",
                event.getEventType(), event.getRestaurantId());

        switch (event.getEventType()) {
            case RESTAURANT_CLOSED -> handleRestaurantClosed(event);
            case RESTAURANT_SUSPENDED -> handleRestaurantSuspended(event);
            case RESTAURANT_DELETED -> handleRestaurantDeleted(event);
            case RESTAURANT_ORDERS_PAUSED -> handleOrdersPaused(event);
            case RESTAURANT_OPENED -> handleRestaurantOpened(event);
            case RESTAURANT_ORDERS_RESUMED -> handleOrdersResumed(event);
            default -> log.debug("Event type {} ignored", event.getEventType());
        }
    }

    /**
     * Quando restaurante fecha, cancela pedidos PENDING
     */
    private void handleRestaurantClosed(RestaurantEvent event) {
        log.info("Restaurant closed: {}. Cancelling pending orders...", event.getRestaurantId());

        cancelPendingOrders(event, "Restaurante fechou")
                .subscribe(
                        count -> log.info("Cancelled {} pending orders for restaurant {}",
                                count, event.getRestaurantId()),
                        error -> log.error("Error cancelling orders: {}", error.getMessage())
                );
    }

    /**
     * Quando restaurante é suspenso, cancela pedidos PENDING e CONFIRMED
     */
    private void handleRestaurantSuspended(RestaurantEvent event) {
        log.warn("Restaurant suspended: {}. Cancelling pending and confirmed orders...",
                event.getRestaurantId());

        orderRepository.findActiveOrdersByRestaurant(event.getRestaurantId())
                .filter(order -> order.getStatus() == OrderStatus.PENDING ||
                        order.getStatus() == OrderStatus.CONFIRMED)
                .flatMap(order -> {
                    order.cancel("Restaurante suspenso");
                    order.setUpdatedAt(LocalDateTime.now());
                    return orderRepository.save(order);
                })
                .count()
                .subscribe(
                        count -> log.info("Cancelled {} orders due to restaurant suspension: {}",
                                count, event.getRestaurantId()),
                        error -> log.error("Error handling restaurant suspension: {}", error.getMessage())
                );
    }

    /**
     * Quando restaurante é deletado, cancela todos os pedidos ativos
     */
    private void handleRestaurantDeleted(RestaurantEvent event) {
        log.warn("Restaurant deleted: {}. Cancelling all active orders...", event.getRestaurantId());

        orderRepository.findActiveOrdersByRestaurant(event.getRestaurantId())
                .flatMap(order -> {
                    order.cancel("Restaurante não está mais disponível");
                    order.setUpdatedAt(LocalDateTime.now());
                    return orderRepository.save(order);
                })
                .count()
                .subscribe(
                        count -> log.info("Cancelled {} orders due to restaurant deletion: {}",
                                count, event.getRestaurantId()),
                        error -> log.error("Error handling restaurant deletion: {}", error.getMessage())
                );
    }

    /**
     * Quando pedidos são pausados, apenas loga (não cancela pedidos existentes)
     */
    private void handleOrdersPaused(RestaurantEvent event) {
        log.info("Restaurant {} paused new orders. Existing orders will continue.",
                event.getRestaurantId());
        // Não cancela pedidos existentes, apenas impede novos
        // A validação de novos pedidos deve ser feita no OrderService.createOrder()
    }

    /**
     * Quando restaurante abre, apenas loga
     */
    private void handleRestaurantOpened(RestaurantEvent event) {
        log.info("Restaurant {} is now open", event.getRestaurantId());
    }

    /**
     * Quando pedidos são retomados, apenas loga
     */
    private void handleOrdersResumed(RestaurantEvent event) {
        log.info("Restaurant {} resumed accepting orders", event.getRestaurantId());
    }

    private Mono<Long> cancelPendingOrders(RestaurantEvent event, String reason) {
        return orderRepository.findActiveOrdersByRestaurant(event.getRestaurantId())
                .filter(order -> order.getStatus() == OrderStatus.PENDING)
                .flatMap(order -> {
                    order.cancel(reason);
                    order.setUpdatedAt(LocalDateTime.now());
                    return orderRepository.save(order);
                })
                .count();
    }
}