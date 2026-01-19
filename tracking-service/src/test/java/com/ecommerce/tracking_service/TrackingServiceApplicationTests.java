// tracking-service/src/test/java/com/ecommerce/tracking/TrackingServiceApplicationTests.java
package com.ecommerce.tracking_service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrackingServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertDoesNotThrow(() -> assertNotNull(
                Class.forName("com.ecommerce.tracking.TrackingServiceApplication")
        ));
    }
}