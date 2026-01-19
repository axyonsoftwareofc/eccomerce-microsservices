// config-server/src/test/java/com/ecommerce/config/ConfigServerApplicationTests.java
package com.ecommerce.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfigServerApplicationTests {

    @Test
    void applicationClassExists() {
        assertDoesNotThrow(() -> {
            Class<?> clazz = Class.forName("com.ecommerce.config.ConfigServerApplication");
            assertNotNull(clazz);
        });
    }
}