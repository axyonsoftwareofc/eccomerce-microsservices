// user-service/src/test/java/com/ecommerce/user/UserServiceApplicationTests.java
package com.ecommerce.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertDoesNotThrow(() -> assertNotNull(
                Class.forName("com.ecommerce.user.UserServiceApplication")
        ));
    }
}