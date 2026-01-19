// restaurant-service/src/test/java/com/ecommerce/restaurant/config/TestConfig.java
package com.ecommerce.restaurant.config;

import com.ecommerce.restaurant.infrastructure.messaging.producer.RestaurantEventProducer;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RestaurantEventProducer restaurantEventProducer() {
        RestaurantEventProducer mock = Mockito.mock(RestaurantEventProducer.class);
        // Mock todos os métodos void
        doNothing().when(mock).sendRestaurantCreated(any());
        doNothing().when(mock).sendRestaurantUpdated(any());
        doNothing().when(mock).sendRestaurantDeleted(any(), any());
        doNothing().when(mock).sendRestaurantOpened(any());
        doNothing().when(mock).sendRestaurantClosed(any());
        doNothing().when(mock).sendRestaurantActivated(any());
        doNothing().when(mock).sendRestaurantSuspended(any());
        doNothing().when(mock).sendOrdersPaused(any());
        doNothing().when(mock).sendOrdersResumed(any());
        return mock;
    }
}