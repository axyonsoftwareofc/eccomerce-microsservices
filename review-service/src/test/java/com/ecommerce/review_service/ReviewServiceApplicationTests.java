// review-service/src/test/java/com/ecommerce/review/ReviewServiceApplicationTests.java
package com.ecommerce.review_service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertDoesNotThrow(() -> assertNotNull(
                Class.forName("com.ecommerce.review.ReviewServiceApplication")
        ));
    }
}