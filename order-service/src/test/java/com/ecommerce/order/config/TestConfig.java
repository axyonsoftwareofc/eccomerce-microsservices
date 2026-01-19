// order-service/src/test/java/com/ecommerce/order/config/TestConfig.java
package com.ecommerce.order.config;

import com.ecommerce.order.infrastructure.messaging.producer.OrderEventProducer;
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
    public OrderEventProducer orderEventProducer() {
        OrderEventProducer mock = Mockito.mock(OrderEventProducer.class);
        doNothing().when(mock).sendOrderCreated(any());
        doNothing().when(mock).sendOrderStatusChanged(any(), any());
        doNothing().when(mock).sendOrderCancelled(any());
        return mock;
    }
}