// restaurant-service/src/test/java/com/ecommerce/restaurant/domain/entity/RestaurantTest.java
package com.ecommerce.restaurant.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Restaurant Entity Tests")
class RestaurantTest {

    private Restaurant createRestaurant() {
        return Restaurant.builder()
                .id(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .name("Test Restaurant")
                .status(RestaurantStatus.ACTIVE)
                .isOpen(true)
                .isAcceptingOrders(true)
                .latitude(new BigDecimal("-23.550520"))
                .longitude(new BigDecimal("-46.633308"))
                .deliveryRadiusKm(new BigDecimal("5.0"))
                .opensAt(LocalTime.of(8, 0))
                .closesAt(LocalTime.of(22, 0))
                .build();
    }

    @Nested
    @DisplayName("isCurrentlyOpen()")
    class IsCurrentlyOpenTests {

        @Test
        @DisplayName("Should return false when status is not ACTIVE")
        void shouldReturnFalseWhenStatusIsNotActive() {
            Restaurant restaurant = createRestaurant();
            restaurant.setStatus(RestaurantStatus.PENDING_APPROVAL);

            assertFalse(restaurant.isCurrentlyOpen());
        }

        @Test
        @DisplayName("Should return false when isOpen is false")
        void shouldReturnFalseWhenIsOpenIsFalse() {
            Restaurant restaurant = createRestaurant();
            restaurant.setIsOpen(false);

            assertFalse(restaurant.isCurrentlyOpen());
        }

        @Test
        @DisplayName("Should return true when open and within hours")
        void shouldReturnTrueWhenOpenAndWithinHours() {
            Restaurant restaurant = createRestaurant();
            restaurant.setOpensAt(LocalTime.of(0, 0));
            restaurant.setClosesAt(LocalTime.of(23, 59));

            assertTrue(restaurant.isCurrentlyOpen());
        }
    }

    @Nested
    @DisplayName("canAcceptOrder()")
    class CanAcceptOrderTests {

        @Test
        @DisplayName("Should return true when all conditions are met")
        void shouldReturnTrueWhenAllConditionsAreMet() {
            Restaurant restaurant = createRestaurant();

            assertTrue(restaurant.canAcceptOrder());
        }

        @Test
        @DisplayName("Should return false when not accepting orders")
        void shouldReturnFalseWhenNotAcceptingOrders() {
            Restaurant restaurant = createRestaurant();
            restaurant.setIsAcceptingOrders(false);

            assertFalse(restaurant.canAcceptOrder());
        }

        @Test
        @DisplayName("Should return false when status is not ACTIVE")
        void shouldReturnFalseWhenStatusIsNotActive() {
            Restaurant restaurant = createRestaurant();
            restaurant.setStatus(RestaurantStatus.SUSPENDED);

            assertFalse(restaurant.canAcceptOrder());
        }
    }

    @Nested
    @DisplayName("deliversToLocation()")
    class DeliversToLocationTests {

        @Test
        @DisplayName("Should return true when location is within radius")
        void shouldReturnTrueWhenLocationIsWithinRadius() {
            Restaurant restaurant = createRestaurant();
            // Approximately 1km away
            BigDecimal customerLat = new BigDecimal("-23.551520");
            BigDecimal customerLng = new BigDecimal("-46.634308");

            assertTrue(restaurant.deliversToLocation(customerLat, customerLng));
        }

        @Test
        @DisplayName("Should return false when location is outside radius")
        void shouldReturnFalseWhenLocationIsOutsideRadius() {
            Restaurant restaurant = createRestaurant();
            // Approximately 50km away
            BigDecimal customerLat = new BigDecimal("-23.950520");
            BigDecimal customerLng = new BigDecimal("-46.933308");

            assertFalse(restaurant.deliversToLocation(customerLat, customerLng));
        }

        @Test
        @DisplayName("Should return true when deliveryRadiusKm is null")
        void shouldReturnTrueWhenDeliveryRadiusIsNull() {
            Restaurant restaurant = createRestaurant();
            restaurant.setDeliveryRadiusKm(null);

            assertTrue(restaurant.deliversToLocation(
                    new BigDecimal("-23.950520"),
                    new BigDecimal("-46.933308")
            ));
        }
    }

    @Nested
    @DisplayName("Status Operations")
    class StatusOperationsTests {

        @Test
        @DisplayName("Should open restaurant")
        void shouldOpenRestaurant() {
            Restaurant restaurant = createRestaurant();
            restaurant.setIsOpen(false);

            restaurant.open();

            assertTrue(restaurant.getIsOpen());
        }

        @Test
        @DisplayName("Should close restaurant")
        void shouldCloseRestaurant() {
            Restaurant restaurant = createRestaurant();

            restaurant.close();

            assertFalse(restaurant.getIsOpen());
        }

        @Test
        @DisplayName("Should activate restaurant")
        void shouldActivateRestaurant() {
            Restaurant restaurant = createRestaurant();
            restaurant.setStatus(RestaurantStatus.PENDING_APPROVAL);

            restaurant.activate();

            assertEquals(RestaurantStatus.ACTIVE, restaurant.getStatus());
        }

        @Test
        @DisplayName("Should suspend restaurant")
        void shouldSuspendRestaurant() {
            Restaurant restaurant = createRestaurant();

            restaurant.suspend();

            assertEquals(RestaurantStatus.SUSPENDED, restaurant.getStatus());
        }

        @Test
        @DisplayName("Should pause orders")
        void shouldPauseOrders() {
            Restaurant restaurant = createRestaurant();

            restaurant.pauseOrders();

            assertFalse(restaurant.getIsAcceptingOrders());
        }

        @Test
        @DisplayName("Should resume orders")
        void shouldResumeOrders() {
            Restaurant restaurant = createRestaurant();
            restaurant.setIsAcceptingOrders(false);

            restaurant.resumeOrders();

            assertTrue(restaurant.getIsAcceptingOrders());
        }
    }

    @Nested
    @DisplayName("Rating and Orders")
    class RatingAndOrdersTests {

        @Test
        @DisplayName("Should update rating")
        void shouldUpdateRating() {
            Restaurant restaurant = createRestaurant();

            restaurant.updateRating(new BigDecimal("4.5"), 100);

            assertEquals(new BigDecimal("4.5"), restaurant.getRating());
            assertEquals(100, restaurant.getTotalReviews());
        }

        @Test
        @DisplayName("Should increment order count")
        void shouldIncrementOrderCount() {
            Restaurant restaurant = createRestaurant();
            restaurant.setTotalOrders(10);

            restaurant.incrementOrderCount();

            assertEquals(11, restaurant.getTotalOrders());
        }

        @Test
        @DisplayName("Should increment order count from null")
        void shouldIncrementOrderCountFromNull() {
            Restaurant restaurant = createRestaurant();
            restaurant.setTotalOrders(null);

            restaurant.incrementOrderCount();

            assertEquals(1, restaurant.getTotalOrders());
        }
    }
}