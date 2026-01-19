// delivery-service/src/test/java/com/ecommerce/delivery/DeliveryServiceApplicationTests.java
package com.ecommerce.delivery_service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeliveryServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertDoesNotThrow(() -> assertNotNull(
                Class.forName("com.ecommerce.delivery.DeliveryServiceApplication")
        ));
    }
}