// order-service/src/test/java/com/ecommerce/order/config/TestConfig.java
package com.ecommerce.order.config;

import com.ecommerce.order.infrastructure.messaging.producer.OrderEventProducer;
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
    public OrderEventProducer orderEventProducer() {
        OrderEventProducer mockProducer = mock(OrderEventProducer.class);
        doNothing().when(mockProducer).sendOrderCreated(any());
        doNothing().when(mockProducer).sendOrderStatusChanged(any(), any());
        doNothing().when(mockProducer).sendOrderCancelled(any());
        return mockProducer;
    }
}