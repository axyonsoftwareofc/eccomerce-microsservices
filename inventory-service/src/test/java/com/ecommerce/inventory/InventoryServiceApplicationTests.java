// inventory-service/src/test/java/com/ecommerce/inventory/InventoryServiceApplicationTests.java
package com.ecommerce.inventory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertDoesNotThrow(() -> assertNotNull(
                Class.forName("com.ecommerce.inventory.InventoryServiceApplication")
        ));
    }
}