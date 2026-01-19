// notification-service/src/test/java/com/ecommerce/notification/NotificationServiceApplicationTests.java
package com.ecommerce.notification;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertDoesNotThrow(() -> assertNotNull(
                Class.forName("com.ecommerce.notification.NotificationServiceApplication")
        ));
    }
}