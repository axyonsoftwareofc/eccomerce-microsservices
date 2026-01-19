// api-gateway/src/test/java/com/ecommerce/gateway/ApiGatewayApplicationTests.java
package com.ecommerce.gateway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApiGatewayApplicationTests {

    @Test
    void applicationClassExists() {
        assertDoesNotThrow(() -> {
            Class<?> clazz = Class.forName("com.ecommerce.gateway.ApiGatewayApplication");
            assertNotNull(clazz);
        });
    }
}