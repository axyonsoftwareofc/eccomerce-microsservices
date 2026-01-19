// menu-service/src/test/java/com/ecommerce/menu/domain/entity/MenuCategoryTest.java
package com.ecommerce.menu.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MenuCategory Entity Tests")
class MenuCategoryTest {

    private MenuCategory createCategory() {
        return MenuCategory.builder()
                .id(UUID.randomUUID())
                .restaurantId(UUID.randomUUID())
                .name("Test Category")
                .description("Test Description")
                .isActive(true)
                .displayOrder(1)
                .build();
    }

    @Nested
    @DisplayName("isCurrentlyAvailable()")
    class IsCurrentlyAvailableTests {

        @Test
        @DisplayName("Should return true when active and no time restriction")
        void shouldReturnTrueWhenActiveAndNoTimeRestriction() {
            MenuCategory category = createCategory();
            assertTrue(category.isCurrentlyAvailable());
        }

        @Test
        @DisplayName("Should return false when not active")
        void shouldReturnFalseWhenNotActive() {
            MenuCategory category = createCategory();
            category.setIsActive(false);
            assertFalse(category.isCurrentlyAvailable());
        }

        @Test
        @DisplayName("Should check time availability")
        void shouldCheckTimeAvailability() {
            MenuCategory category = createCategory();
            category.setAvailableFrom(LocalTime.of(0, 0));
            category.setAvailableUntil(LocalTime.of(23, 59));
            assertTrue(category.isCurrentlyAvailable());
        }
    }

    @Nested
    @DisplayName("Status Operations")
    class StatusOperationsTests {

        @Test
        @DisplayName("Should activate category")
        void shouldActivateCategory() {
            MenuCategory category = createCategory();
            category.setIsActive(false);

            category.activate();

            assertTrue(category.getIsActive());
        }

        @Test
        @DisplayName("Should deactivate category")
        void shouldDeactivateCategory() {
            MenuCategory category = createCategory();

            category.deactivate();

            assertFalse(category.getIsActive());
        }
    }
}