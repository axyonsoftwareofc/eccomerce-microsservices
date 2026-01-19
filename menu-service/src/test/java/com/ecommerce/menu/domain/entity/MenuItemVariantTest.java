// menu-service/src/test/java/com/ecommerce/menu/domain/entity/MenuItemVariantTest.java
package com.ecommerce.menu.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MenuItemVariant Entity Tests")
class MenuItemVariantTest {

    @Test
    @DisplayName("Should return variant price when set")
    void shouldReturnVariantPriceWhenSet() {
        MenuItemVariant variant = MenuItemVariant.builder()
                .id(UUID.randomUUID())
                .menuItemId(UUID.randomUUID())
                .name("Grande")
                .variantType(VariantType.SIZE)
                .price(new BigDecimal("39.90"))
                .build();

        BigDecimal basePrice = new BigDecimal("29.90");
        BigDecimal finalPrice = variant.calculateFinalPrice(basePrice);

        assertEquals(new BigDecimal("39.90"), finalPrice);
    }

    @Test
    @DisplayName("Should apply price modifier")
    void shouldApplyPriceModifier() {
        MenuItemVariant variant = MenuItemVariant.builder()
                .id(UUID.randomUUID())
                .menuItemId(UUID.randomUUID())
                .name("Grande")
                .variantType(VariantType.SIZE)
                .priceModifier(new BigDecimal("10.00"))
                .build();

        BigDecimal basePrice = new BigDecimal("29.90");
        BigDecimal finalPrice = variant.calculateFinalPrice(basePrice);

        assertEquals(0, new BigDecimal("39.90").compareTo(finalPrice));
    }

    @Test
    @DisplayName("Should return base price when no variant price or modifier")
    void shouldReturnBasePriceWhenNoVariantPriceOrModifier() {
        MenuItemVariant variant = MenuItemVariant.builder()
                .id(UUID.randomUUID())
                .menuItemId(UUID.randomUUID())
                .name("Normal")
                .variantType(VariantType.SIZE)
                .build();

        BigDecimal basePrice = new BigDecimal("29.90");
        BigDecimal finalPrice = variant.calculateFinalPrice(basePrice);

        assertEquals(basePrice, finalPrice);
    }

    @Test
    @DisplayName("Should mark as available")
    void shouldMarkAsAvailable() {
        MenuItemVariant variant = MenuItemVariant.builder()
                .id(UUID.randomUUID())
                .menuItemId(UUID.randomUUID())
                .name("Grande")
                .variantType(VariantType.SIZE)
                .isAvailable(false)
                .build();

        variant.markAsAvailable();

        assertTrue(variant.getIsAvailable());
    }

    @Test
    @DisplayName("Should mark as unavailable")
    void shouldMarkAsUnavailable() {
        MenuItemVariant variant = MenuItemVariant.builder()
                .id(UUID.randomUUID())
                .menuItemId(UUID.randomUUID())
                .name("Grande")
                .variantType(VariantType.SIZE)
                .isAvailable(true)
                .build();

        variant.markAsUnavailable();

        assertFalse(variant.getIsAvailable());
    }
}