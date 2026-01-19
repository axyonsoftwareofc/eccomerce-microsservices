// payment-service/src/test/java/com/ecommerce/payment/PaymentServiceApplicationTests.java
package com.ecommerce.payment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertDoesNotThrow(() -> assertNotNull(
                Class.forName("com.ecommerce.payment.PaymentServiceApplication")
        ));
    }
}