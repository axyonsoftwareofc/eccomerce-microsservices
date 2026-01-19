// menu-service/src/test/java/com/ecommerce/menu/domain/entity/MenuItemTest.java
package com.ecommerce.menu.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MenuItem Entity Tests")
class MenuItemTest {

    private MenuItem createMenuItem() {
        return MenuItem.builder()
                .id(UUID.randomUUID())
                .restaurantId(UUID.randomUUID())
                .categoryId(UUID.randomUUID())
                .name("Test Item")
                .price(new BigDecimal("50.00"))
                .isAvailable(true)
                .isFeatured(false)
                .stockQuantity(10)
                .availableFrom(LocalTime.of(0, 0))
                .availableUntil(LocalTime.of(23, 59))
                .build();
    }

    @Nested
    @DisplayName("isCurrentlyAvailable()")
    class IsCurrentlyAvailableTests {

        @Test
        @DisplayName("Should return false when isAvailable is false")
        void shouldReturnFalseWhenIsAvailableIsFalse() {
            MenuItem item = createMenuItem();
            item.setIsAvailable(false);

            assertFalse(item.isCurrentlyAvailable());
        }

        @Test
        @DisplayName("Should return false when stock is zero")
        void shouldReturnFalseWhenStockIsZero() {
            MenuItem item = createMenuItem();
            item.setStockQuantity(0);

            assertFalse(item.isCurrentlyAvailable());
        }

        @Test
        @DisplayName("Should return true when available and in stock")
        void shouldReturnTrueWhenAvailableAndInStock() {
            MenuItem item = createMenuItem();

            assertTrue(item.isCurrentlyAvailable());
        }

        @Test
        @DisplayName("Should return true when stock is null (unlimited)")
        void shouldReturnTrueWhenStockIsNull() {
            MenuItem item = createMenuItem();
            item.setStockQuantity(null);

            assertTrue(item.isCurrentlyAvailable());
        }
    }

    @Nested
    @DisplayName("getFinalPrice()")
    class GetFinalPriceTests {

        @Test
        @DisplayName("Should return price when no discount")
        void shouldReturnPriceWhenNoDiscount() {
            MenuItem item = createMenuItem();
            item.setDiscountPercentage(null);

            assertEquals(new BigDecimal("50.00"), item.getFinalPrice());
        }

        @Test
        @DisplayName("Should return discounted price")
        void shouldReturnDiscountedPrice() {
            MenuItem item = createMenuItem();
            item.setPrice(new BigDecimal("100.00"));
            item.setDiscountPercentage(new BigDecimal("20"));

            assertEquals(0, item.getFinalPrice().compareTo(new BigDecimal("80.00")));
        }

        @Test
        @DisplayName("Should return price when discount is zero")
        void shouldReturnPriceWhenDiscountIsZero() {
            MenuItem item = createMenuItem();
            item.setDiscountPercentage(BigDecimal.ZERO);

            assertEquals(new BigDecimal("50.00"), item.getFinalPrice());
        }
    }

    @Nested
    @DisplayName("hasDiscount()")
    class HasDiscountTests {

        @Test
        @DisplayName("Should return true when has discount")
        void shouldReturnTrueWhenHasDiscount() {
            MenuItem item = createMenuItem();
            item.setDiscountPercentage(new BigDecimal("10"));

            assertTrue(item.hasDiscount());
        }

        @Test
        @DisplayName("Should return false when no discount")
        void shouldReturnFalseWhenNoDiscount() {
            MenuItem item = createMenuItem();
            item.setDiscountPercentage(null);

            assertFalse(item.hasDiscount());
        }

        @Test
        @DisplayName("Should return false when discount is zero")
        void shouldReturnFalseWhenDiscountIsZero() {
            MenuItem item = createMenuItem();
            item.setDiscountPercentage(BigDecimal.ZERO);

            assertFalse(item.hasDiscount());
        }
    }

    @Nested
    @DisplayName("Stock Operations")
    class StockOperationsTests {

        @Test
        @DisplayName("Should decrement stock")
        void shouldDecrementStock() {
            MenuItem item = createMenuItem();
            item.setStockQuantity(10);

            item.decrementStock(3);

            assertEquals(7, item.getStockQuantity());
        }

        @Test
        @DisplayName("Should not go below zero when decrementing")
        void shouldNotGoBelowZeroWhenDecrementing() {
            MenuItem item = createMenuItem();
            item.setStockQuantity(5);

            item.decrementStock(10);

            assertEquals(0, item.getStockQuantity());
        }

        @Test
        @DisplayName("Should mark as unavailable when stock reaches zero")
        void shouldMarkAsUnavailableWhenStockReachesZero() {
            MenuItem item = createMenuItem();
            item.setStockQuantity(3);

            item.decrementStock(3);

            assertFalse(item.getIsAvailable());
        }

        @Test
        @DisplayName("Should increment stock")
        void shouldIncrementStock() {
            MenuItem item = createMenuItem();
            item.setStockQuantity(10);

            item.incrementStock(5);

            assertEquals(15, item.getStockQuantity());
        }

        @Test
        @DisplayName("Should mark as available when stock is added")
        void shouldMarkAsAvailableWhenStockIsAdded() {
            MenuItem item = createMenuItem();
            item.setStockQuantity(0);
            item.setIsAvailable(false);

            item.incrementStock(5);

            assertTrue(item.getIsAvailable());
        }
    }

    @Nested
    @DisplayName("Status Operations")
    class StatusOperationsTests {

        @Test
        @DisplayName("Should mark as available")
        void shouldMarkAsAvailable() {
            MenuItem item = createMenuItem();
            item.setIsAvailable(false);

            item.markAsAvailable();

            assertTrue(item.getIsAvailable());
        }

        @Test
        @DisplayName("Should mark as unavailable")
        void shouldMarkAsUnavailable() {
            MenuItem item = createMenuItem();

            item.markAsUnavailable();

            assertFalse(item.getIsAvailable());
        }

        @Test
        @DisplayName("Should feature item")
        void shouldFeatureItem() {
            MenuItem item = createMenuItem();

            item.feature();

            assertTrue(item.getIsFeatured());
        }

        @Test
        @DisplayName("Should unfeature item")
        void shouldUnfeatureItem() {
            MenuItem item = createMenuItem();
            item.setIsFeatured(true);

            item.unfeature();

            assertFalse(item.getIsFeatured());
        }

        @Test
        @DisplayName("Should increment order count")
        void shouldIncrementOrderCount() {
            MenuItem item = createMenuItem();
            item.setTotalOrders(10);

            item.incrementOrderCount();

            assertEquals(11, item.getTotalOrders());
        }

        @Test
        @DisplayName("Should increment order count from null")
        void shouldIncrementOrderCountFromNull() {
            MenuItem item = createMenuItem();
            item.setTotalOrders(null);

            item.incrementOrderCount();

            assertEquals(1, item.getTotalOrders());
        }
    }
}