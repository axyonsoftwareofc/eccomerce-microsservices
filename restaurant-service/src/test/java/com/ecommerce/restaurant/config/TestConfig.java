// restaurant-service/src/test/java/com/ecommerce/restaurant/config/TestConfig.java
package com.ecommerce.restaurant.config;

import com.ecommerce.restaurant.infrastructure.messaging.producer.RestaurantEventProducer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RestaurantEventProducer restaurantEventProducer() {
        RestaurantEventProducer mockProducer = mock(RestaurantEventProducer.class);
        doNothing().when(mockProducer).sendRestaurantCreated(any());
        doNothing().when(mockProducer).sendRestaurantUpdated(any());
        doNothing().when(mockProducer).sendRestaurantDeleted(any(), any());
        doNothing().when(mockProducer).sendRestaurantOpened(any());
        doNothing().when(mockProducer).sendRestaurantClosed(any());
        doNothing().when(mockProducer).sendRestaurantActivated(any());
        doNothing().when(mockProducer).sendRestaurantSuspended(any());
        doNothing().when(mockProducer).sendOrdersPaused(any());
        doNothing().when(mockProducer).sendOrdersResumed(any());
        return mockProducer;
    }
}