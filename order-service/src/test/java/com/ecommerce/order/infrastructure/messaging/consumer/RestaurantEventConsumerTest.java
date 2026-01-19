// order-service/src/test/java/com/ecommerce/order/infrastructure/messaging/consumer/RestaurantEventConsumerTest.java
package com.ecommerce.order.infrastructure.messaging.consumer;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderStatus;
import com.ecommerce.order.infrastructure.messaging.event.RestaurantEvent;
import com.ecommerce.order.infrastructure.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestaurantEventConsumer Tests")
class RestaurantEventConsumerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private RestaurantEventConsumer consumer;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private UUID restaurantId;
    private RestaurantEvent event;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
    }

    private Order createOrder(OrderStatus status) {
        return Order.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .restaurantId(restaurantId)
                .status(status)
                .deliveryStreet("Test Street")
                .deliveryNumber("123")
                .deliveryNeighborhood("Test")
                .deliveryCity("Test City")
                .deliveryState("SP")
                .deliveryZipCode("12345-678")
                .subtotal(new BigDecimal("100.00"))
                .total(new BigDecimal("105.00"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private RestaurantEvent createEvent(RestaurantEvent.EventType eventType) {
        return RestaurantEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(eventType)
                .restaurantId(restaurantId)
                .ownerId(UUID.randomUUID())
                .name("Test Restaurant")
                .isOpen(true)
                .isAcceptingOrders(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Restaurant Closed Event")
    class RestaurantClosedTests {

        @Test
        @DisplayName("Should cancel pending orders when restaurant closes")
        void shouldCancelPendingOrdersWhenRestaurantCloses() {
            // Arrange
            Order pendingOrder = createOrder(OrderStatus.PENDING);
            event = createEvent(RestaurantEvent.EventType.RESTAURANT_CLOSED);

            when(orderRepository.findActiveOrdersByRestaurant(restaurantId))
                    .thenReturn(Flux.just(pendingOrder));
            when(orderRepository.save(any(Order.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            // Act
            consumer.handleRestaurantEvent(event);

            // Assert (aguarda processamento assíncrono)
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }

            verify(orderRepository).findActiveOrdersByRestaurant(restaurantId);
            verify(orderRepository).save(orderCaptor.capture());

            Order cancelledOrder = orderCaptor.getValue();
            assertThat(cancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
            assertThat(cancelledOrder.getCancellationReason()).isEqualTo("Restaurante fechou");
        }

        @Test
        @DisplayName("Should not cancel orders in PREPARING status")
        void shouldNotCancelOrdersInPreparingStatus() {
            // Arrange
            Order preparingOrder = createOrder(OrderStatus.PREPARING);
            event = createEvent(RestaurantEvent.EventType.RESTAURANT_CLOSED);

            when(orderRepository.findActiveOrdersByRestaurant(restaurantId))
                    .thenReturn(Flux.just(preparingOrder));

            // Act
            consumer.handleRestaurantEvent(event);

            // Assert
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }

            verify(orderRepository).findActiveOrdersByRestaurant(restaurantId);
            verify(orderRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Restaurant Suspended Event")
    class RestaurantSuspendedTests {

        @Test
        @DisplayName("Should cancel pending and confirmed orders when restaurant is suspended")
        void shouldCancelPendingAndConfirmedOrdersWhenSuspended() {
            // Arrange
            Order pendingOrder = createOrder(OrderStatus.PENDING);
            Order confirmedOrder = createOrder(OrderStatus.CONFIRMED);
            event = createEvent(RestaurantEvent.EventType.RESTAURANT_SUSPENDED);

            when(orderRepository.findActiveOrdersByRestaurant(restaurantId))
                    .thenReturn(Flux.just(pendingOrder, confirmedOrder));
            when(orderRepository.save(any(Order.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            // Act
            consumer.handleRestaurantEvent(event);

            // Assert
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }

            verify(orderRepository, times(2)).save(orderCaptor.capture());

            assertThat(orderCaptor.getAllValues())
                    .allMatch(order -> order.getStatus() == OrderStatus.CANCELLED)
                    .allMatch(order -> order.getCancellationReason().equals("Restaurante suspenso"));
        }
    }

    @Nested
    @DisplayName("Restaurant Deleted Event")
    class RestaurantDeletedTests {

        @Test
        @DisplayName("Should cancel all active orders when restaurant is deleted")
        void shouldCancelAllActiveOrdersWhenDeleted() {
            // Arrange - apenas 2 pedidos que PODEM ser cancelados
            Order pendingOrder = createOrder(OrderStatus.PENDING);
            Order confirmedOrder = createOrder(OrderStatus.CONFIRMED);
            // PREPARING não pode ser cancelado pelo método cancel()

            event = createEvent(RestaurantEvent.EventType.RESTAURANT_DELETED);

            when(orderRepository.findActiveOrdersByRestaurant(restaurantId))
                    .thenReturn(Flux.just(pendingOrder, confirmedOrder));
            when(orderRepository.save(any(Order.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            // Act
            consumer.handleRestaurantEvent(event);

            // Assert
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }

            verify(orderRepository, times(2)).save(orderCaptor.capture());

            assertThat(orderCaptor.getAllValues())
                    .allMatch(order -> order.getStatus() == OrderStatus.CANCELLED);
        }

        @Nested
        @DisplayName("Other Events")
        class OtherEventsTests {

            @Test
            @DisplayName("Should log when orders are paused but not cancel orders")
            void shouldNotCancelOrdersWhenPaused() {
                // Arrange
                event = createEvent(RestaurantEvent.EventType.RESTAURANT_ORDERS_PAUSED);

                // Act
                consumer.handleRestaurantEvent(event);

                // Assert
                verify(orderRepository, never()).findActiveOrdersByRestaurant(any());
                verify(orderRepository, never()).save(any());
            }

            @Test
            @DisplayName("Should log when restaurant opens")
            void shouldLogWhenRestaurantOpens() {
                // Arrange
                event = createEvent(RestaurantEvent.EventType.RESTAURANT_OPENED);

                // Act
                consumer.handleRestaurantEvent(event);

                // Assert
                verify(orderRepository, never()).findActiveOrdersByRestaurant(any());
            }
        }
    }
}