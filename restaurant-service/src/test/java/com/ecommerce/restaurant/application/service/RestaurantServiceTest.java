// restaurant-service/src/test/java/com/ecommerce/restaurant/application/service/RestaurantServiceTest.java
package com.ecommerce.restaurant.application.service;

import com.ecommerce.restaurant.application.dto.request.CreateRestaurantRequest;
import com.ecommerce.restaurant.application.dto.request.UpdateRestaurantRequest;
import com.ecommerce.restaurant.application.dto.response.RestaurantResponse;
import com.ecommerce.restaurant.domain.entity.RestaurantStatus;
import com.ecommerce.restaurant.domain.exception.RestaurantNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("RestaurantService Tests")
class RestaurantServiceTest {

    @Autowired
    private RestaurantService restaurantService;

    // ==================== HELPER METHODS ====================

    private CreateRestaurantRequest createValidRequest() {
        return CreateRestaurantRequest.builder()
                .ownerId(UUID.randomUUID())
                .name("Pizzaria do João")
                .description("A melhor pizza da cidade")
                .phone("11999999999")
                .email("contato@pizzariadojoao.com")
                .addressStreet("Rua das Pizzas")
                .addressNumber("123")
                .addressComplement("Loja A")
                .addressNeighborhood("Centro")
                .addressCity("São Paulo")
                .addressState("SP")
                .addressZipCode("01234-567")
                .latitude(new BigDecimal("-23.550520"))
                .longitude(new BigDecimal("-46.633308"))
                .deliveryRadiusKm(new BigDecimal("5.0"))
                .minOrderValue(new BigDecimal("20.00"))
                .deliveryFee(new BigDecimal("5.00"))
                .avgPreparationTime(30)
                .opensAt(LocalTime.of(18, 0))
                .closesAt(LocalTime.of(23, 0))
                .isOpenOnWeekends(true)
                .build();
    }

    // ==================== CREATE TESTS ====================

    @Nested
    @DisplayName("Create Restaurant")
    class CreateRestaurantTests {

        @Test
        @DisplayName("Should create restaurant with valid data")
        void shouldCreateRestaurantWithValidData() {
            // Given
            CreateRestaurantRequest request = createValidRequest();

            // When & Then
            StepVerifier.create(restaurantService.createRestaurant(request))
                    .assertNext(response -> {
                        assert response.getId() != null : "ID should not be null";
                        assert response.getName().equals("Pizzaria do João") : "Name should match";
                        assert response.getStatus() == RestaurantStatus.PENDING_APPROVAL : "Status should be PENDING_APPROVAL";
                        assert response.getIsOpen() == false : "Should not be open initially";
                        assert response.getIsAcceptingOrders() == true : "Should accept orders by default";
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should create restaurant with minimal data")
        void shouldCreateRestaurantWithMinimalData() {
            // Given
            CreateRestaurantRequest request = CreateRestaurantRequest.builder()
                    .ownerId(UUID.randomUUID())
                    .name("Restaurant Minimal")
                    .phone("11999999999")
                    .email("minimal@test.com")
                    .addressStreet("Rua Teste")
                    .addressNumber("1")
                    .addressNeighborhood("Bairro")
                    .addressCity("Cidade")
                    .addressState("SP")
                    .addressZipCode("00000-000")
                    .build();

            // When & Then
            StepVerifier.create(restaurantService.createRestaurant(request))
                    .assertNext(response -> {
                        assert response.getId() != null;
                        assert response.getName().equals("Restaurant Minimal");
                    })
                    .verifyComplete();
        }
    }

    // ==================== READ TESTS ====================

    @Nested
    @DisplayName("Get Restaurant")
    class GetRestaurantTests {

        @Test
        @DisplayName("Should get restaurant by ID")
        void shouldGetRestaurantById() {
            // Given
            CreateRestaurantRequest request = createValidRequest();

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(request)
                                    .flatMap(created -> restaurantService.getRestaurantById(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getName().equals("Pizzaria do João");
                        assert response.getPhone().equals("11999999999");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should throw exception when restaurant not found")
        void shouldThrowExceptionWhenRestaurantNotFound() {
            // Given
            UUID nonExistentId = UUID.randomUUID();

            // When & Then
            StepVerifier.create(restaurantService.getRestaurantById(nonExistentId))
                    .expectError(RestaurantNotFoundException.class)
                    .verify();
        }

        @Test
        @DisplayName("Should get all restaurants")
        void shouldGetAllRestaurants() {
            // Given
            CreateRestaurantRequest request1 = createValidRequest();
            request1.setName("Restaurant 1");

            CreateRestaurantRequest request2 = createValidRequest();
            request2.setName("Restaurant 2");

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(request1)
                                    .then(restaurantService.createRestaurant(request2))
                                    .thenMany(restaurantService.getAllRestaurants())
                                    .collectList()
                    )
                    .assertNext(restaurants -> {
                        assert restaurants.size() >= 2 : "Should have at least 2 restaurants";
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should get restaurants by owner")
        void shouldGetRestaurantsByOwner() {
            // Given
            UUID ownerId = UUID.randomUUID();
            CreateRestaurantRequest request = createValidRequest();
            request.setOwnerId(ownerId);

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(request)
                                    .thenMany(restaurantService.getRestaurantsByOwner(ownerId))
                                    .collectList()
                    )
                    .assertNext(restaurants -> {
                        assert restaurants.size() >= 1;
                        assert restaurants.stream()
                                .allMatch(r -> r.getOwnerId().equals(ownerId));
                    })
                    .verifyComplete();
        }
    }

    // ==================== UPDATE TESTS ====================

    @Nested
    @DisplayName("Update Restaurant")
    class UpdateRestaurantTests {

        @Test
        @DisplayName("Should update restaurant")
        void shouldUpdateRestaurant() {
            // Given
            CreateRestaurantRequest createRequest = createValidRequest();
            UpdateRestaurantRequest updateRequest = UpdateRestaurantRequest.builder()
                    .name("Pizzaria do João - Atualizada")
                    .description("Nova descrição")
                    .deliveryFee(new BigDecimal("7.50"))
                    .build();

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(createRequest)
                                    .flatMap(created ->
                                            restaurantService.updateRestaurant(created.getId(), updateRequest))
                    )
                    .assertNext(response -> {
                        assert response.getName().equals("Pizzaria do João - Atualizada");
                        assert response.getDescription().equals("Nova descrição");
                        assert response.getDeliveryFee().compareTo(new BigDecimal("7.50")) == 0;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent restaurant")
        void shouldThrowExceptionWhenUpdatingNonExistentRestaurant() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            UpdateRestaurantRequest updateRequest = UpdateRestaurantRequest.builder()
                    .name("Updated Name")
                    .build();

            // When & Then
            StepVerifier.create(restaurantService.updateRestaurant(nonExistentId, updateRequest))
                    .expectError(RestaurantNotFoundException.class)
                    .verify();
        }
    }

    // ==================== STATUS TESTS ====================

    @Nested
    @DisplayName("Restaurant Status Operations")
    class StatusOperationsTests {

        @Test
        @DisplayName("Should open restaurant")
        void shouldOpenRestaurant() {
            // Given
            CreateRestaurantRequest request = createValidRequest();

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(request)
                                    .flatMap(created -> restaurantService.openRestaurant(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getIsOpen() == true : "Restaurant should be open";
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should close restaurant")
        void shouldCloseRestaurant() {
            // Given
            CreateRestaurantRequest request = createValidRequest();

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(request)
                                    .flatMap(created -> restaurantService.openRestaurant(created.getId()))
                                    .flatMap(opened -> restaurantService.closeRestaurant(opened.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getIsOpen() == false : "Restaurant should be closed";
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should activate restaurant")
        void shouldActivateRestaurant() {
            // Given
            CreateRestaurantRequest request = createValidRequest();

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(request)
                                    .flatMap(created -> restaurantService.activateRestaurant(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getStatus() == RestaurantStatus.ACTIVE;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should suspend restaurant")
        void shouldSuspendRestaurant() {
            // Given
            CreateRestaurantRequest request = createValidRequest();

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(request)
                                    .flatMap(created -> restaurantService.activateRestaurant(created.getId()))
                                    .flatMap(activated -> restaurantService.suspendRestaurant(activated.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getStatus() == RestaurantStatus.SUSPENDED;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should pause orders")
        void shouldPauseOrders() {
            // Given
            CreateRestaurantRequest request = createValidRequest();

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(request)
                                    .flatMap(created -> restaurantService.pauseOrders(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getIsAcceptingOrders() == false;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should resume orders")
        void shouldResumeOrders() {
            // Given
            CreateRestaurantRequest request = createValidRequest();

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(request)
                                    .flatMap(created -> restaurantService.pauseOrders(created.getId()))
                                    .flatMap(paused -> restaurantService.resumeOrders(paused.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getIsAcceptingOrders() == true;
                    })
                    .verifyComplete();
        }
    }

    // ==================== DELETE TESTS ====================

    @Nested
    @DisplayName("Delete Restaurant")
    class DeleteRestaurantTests {

        @Test
        @DisplayName("Should delete restaurant")
        void shouldDeleteRestaurant() {
            // Given
            CreateRestaurantRequest request = createValidRequest();

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(request)
                                    .flatMap(created ->
                                            restaurantService.deleteRestaurant(created.getId())
                                                    .then(restaurantService.getRestaurantById(created.getId()))
                                    )
                    )
                    .expectError(RestaurantNotFoundException.class)
                    .verify();
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent restaurant")
        void shouldThrowExceptionWhenDeletingNonExistentRestaurant() {
            // Given
            UUID nonExistentId = UUID.randomUUID();

            // When & Then
            StepVerifier.create(restaurantService.deleteRestaurant(nonExistentId))
                    .expectError(RestaurantNotFoundException.class)
                    .verify();
        }
    }

    // ==================== SEARCH TESTS ====================

    @Nested
    @DisplayName("Search Restaurants")
    class SearchRestaurantsTests {

        @Test
        @DisplayName("Should search restaurants by name")
        void shouldSearchRestaurantsByName() {
            // Given
            CreateRestaurantRequest request = createValidRequest();
            request.setName("Unique Pizza Place 12345");

            // When & Then
            StepVerifier.create(
                            restaurantService.createRestaurant(request)
                                    .thenMany(restaurantService.searchRestaurants("Unique Pizza"))
                                    .collectList()
                    )
                    .assertNext(restaurants -> {
                        assert restaurants.stream()
                                .anyMatch(r -> r.getName().contains("Unique Pizza"));
                    })
                    .verifyComplete();
        }
    }
}