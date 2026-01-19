// driver-service/src/test/java/com/ecommerce/driver/DriverServiceApplicationTests.java
package com.ecommerce.driver_service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DriverServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertDoesNotThrow(() -> assertNotNull(
                Class.forName("com.ecommerce.driver.DriverServiceApplication")
        ));
    }
}