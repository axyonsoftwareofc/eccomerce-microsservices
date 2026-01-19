// promotion-service/src/test/java/com/ecommerce/promotion/PromotionServiceApplicationTests.java
package com.ecommerce.promotion_service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PromotionServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertDoesNotThrow(() -> assertNotNull(
                Class.forName("com.ecommerce.promotion.PromotionServiceApplication")
        ));
    }
}