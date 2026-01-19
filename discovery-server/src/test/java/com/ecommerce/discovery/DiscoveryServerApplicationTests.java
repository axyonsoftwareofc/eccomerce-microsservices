// discovery-server/src/test/java/com/ecommerce/discovery/DiscoveryServerApplicationTests.java
package com.ecommerce.discovery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DiscoveryServerApplicationTests {

    @Test
    void applicationClassExists() {
        // Apenas verifica que a classe principal existe e pode ser instanciada
        assertDoesNotThrow(() -> {
            Class.forName("com.ecommerce.discovery.DiscoveryServerApplication");
        });
    }

    @Test
    void mainMethodExists() throws NoSuchMethodException {
        // Verifica que o método main existe
        var mainMethod = DiscoveryServerApplication.class.getMethod("main", String[].class);
        assertDoesNotThrow(() -> mainMethod);
    }
}