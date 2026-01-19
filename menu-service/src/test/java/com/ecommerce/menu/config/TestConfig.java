// menu-service/src/test/java/com/ecommerce/menu/config/TestConfig.java
package com.ecommerce.menu.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public ProducerFactory<String, Object> producerFactory() {
        ProducerFactory<String, Object> factory = mock(ProducerFactory.class);
        when(factory.createProducer()).thenReturn(mock(Producer.class));
        return factory;
    }

    @Bean
    @Primary
    public KafkaAdmin kafkaAdmin() {
        KafkaAdmin admin = mock(KafkaAdmin.class);
        when(admin.describeTopics(any())).thenReturn(java.util.Collections.emptyMap());
        return admin;
    }
}