// restaurant-service/src/test/java/com/ecommerce/restaurant/application/mapper/RestaurantMapperTest.java
package com.ecommerce.restaurant.application.mapper;

import com.ecommerce.restaurant.application.dto.request.CreateRestaurantRequest;
import com.ecommerce.restaurant.application.dto.response.RestaurantResponse;
import com.ecommerce.restaurant.domain.entity.Restaurant;
import com.ecommerce.restaurant.domain.entity.RestaurantStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RestaurantMapper Tests")
class RestaurantMapperTest {

    private RestaurantMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RestaurantMapper();
    }

    @Test
    @DisplayName("Should map CreateRestaurantRequest to Restaurant entity")
    void shouldMapRequestToEntity() {
        // Given
        CreateRestaurantRequest request = CreateRestaurantRequest.builder()
                .ownerId(UUID.randomUUID())
                .name("Test Restaurant")
                .description("Test Description")
                .phone("11999999999")
                .email("test@test.com")
                .addressStreet("Test Street")
                .addressNumber("123")
                .addressNeighborhood("Test Neighborhood")
                .addressCity("Test City")
                .addressState("SP")
                .addressZipCode("12345-678")
                .latitude(new BigDecimal("-23.550520"))
                .longitude(new BigDecimal("-46.633308"))
                .deliveryRadiusKm(new BigDecimal("5.0"))
                .minOrderValue(new BigDecimal("20.00"))
                .deliveryFee(new BigDecimal("5.00"))
                .avgPreparationTime(30)
                .opensAt(LocalTime.of(8, 0))
                .closesAt(LocalTime.of(22, 0))
                .isOpenOnWeekends(true)
                .build();

        // When
        Restaurant entity = mapper.toEntity(request);

        // Then
        assertNotNull(entity);
        assertEquals(request.getOwnerId(), entity.getOwnerId());
        assertEquals(request.getName(), entity.getName());
        assertEquals(request.getDescription(), entity.getDescription());
        assertEquals(request.getPhone(), entity.getPhone());
        assertEquals(request.getEmail(), entity.getEmail());
        assertEquals(request.getAddressStreet(), entity.getAddressStreet());
        assertEquals(request.getDeliveryRadiusKm(), entity.getDeliveryRadiusKm());
        assertEquals(request.getOpensAt(), entity.getOpensAt());
        assertEquals(request.getClosesAt(), entity.getClosesAt());
    }

    @Test
    @DisplayName("Should map Restaurant entity to RestaurantResponse")
    void shouldMapEntityToResponse() {
        // Given
        Restaurant entity = Restaurant.builder()
                .id(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .name("Test Restaurant")
                .description("Test Description")
                .phone("11999999999")
                .email("test@test.com")
                .addressStreet("Test Street")
                .addressNumber("123")
                .addressNeighborhood("Test Neighborhood")
                .addressCity("Test City")
                .addressState("SP")
                .addressZipCode("12345-678")
                .status(RestaurantStatus.ACTIVE)
                .isOpen(true)
                .isAcceptingOrders(true)
                .rating(new BigDecimal("4.5"))
                .totalReviews(100)
                .totalOrders(500)
                .avgPreparationTime(30)
                .avgDeliveryTime(20)
                .opensAt(LocalTime.of(8, 0))
                .closesAt(LocalTime.of(22, 0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        RestaurantResponse response = mapper.toResponse(entity);

        // Then
        assertNotNull(response);
        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getName(), response.getName());
        assertEquals(entity.getStatus(), response.getStatus());
        assertEquals(entity.getIsOpen(), response.getIsOpen());
        assertEquals(50, response.getEstimatedDeliveryTime()); // 30 + 20
        assertNotNull(response.getFullAddress());
        assertTrue(response.getFullAddress().contains("Test Street"));
    }

    @Test
    @DisplayName("Should build full address correctly")
    void shouldBuildFullAddressCorrectly() {
        // Given
        Restaurant entity = Restaurant.builder()
                .id(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .name("Test")
                .addressStreet("Rua das Flores")
                .addressNumber("123")
                .addressComplement("Apto 45")
                .addressNeighborhood("Centro")
                .addressCity("São Paulo")
                .addressState("SP")
                .addressZipCode("01234-567")
                .build();

        // When
        RestaurantResponse response = mapper.toResponse(entity);

        // Then
        String expectedAddress = "Rua das Flores, 123 - Apto 45, Centro, São Paulo - SP, 01234-567";
        assertEquals(expectedAddress, response.getFullAddress());
    }

    @Test
    @DisplayName("Should handle null complement in address")
    void shouldHandleNullComplementInAddress() {
        // Given
        Restaurant entity = Restaurant.builder()
                .id(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .name("Test")
                .addressStreet("Rua das Flores")
                .addressNumber("123")
                .addressComplement(null)
                .addressNeighborhood("Centro")
                .addressCity("São Paulo")
                .addressState("SP")
                .addressZipCode("01234-567")
                .build();

        // When
        RestaurantResponse response = mapper.toResponse(entity);

        // Then
        assertFalse(response.getFullAddress().contains(" - ,"));
    }
}