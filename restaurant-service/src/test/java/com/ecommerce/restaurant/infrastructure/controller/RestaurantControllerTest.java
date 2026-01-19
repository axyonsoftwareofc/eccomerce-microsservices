// restaurant-service/src/test/java/com/ecommerce/restaurant/infrastructure/controller/RestaurantControllerTest.java
package com.ecommerce.restaurant.infrastructure.controller;

import com.ecommerce.restaurant.application.dto.request.CreateRestaurantRequest;
import com.ecommerce.restaurant.application.dto.request.UpdateRestaurantRequest;
import com.ecommerce.restaurant.application.dto.response.RestaurantResponse;
import com.ecommerce.restaurant.domain.entity.RestaurantStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("RestaurantController Tests")
class RestaurantControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_URL = "/api/v1/restaurants";

    // ==================== HELPER METHODS ====================

    private CreateRestaurantRequest createValidRequest() {
        return CreateRestaurantRequest.builder()
                .ownerId(UUID.randomUUID())
                .name("Test Restaurant")
                .description("Test Description")
                .phone("11999999999")
                .email("test@restaurant.com")
                .addressStreet("Test Street")
                .addressNumber("100")
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
                .build();
    }

    private RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        return webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RestaurantResponse.class)
                .returnResult()
                .getResponseBody();
    }

    // ==================== CREATE TESTS ====================

    @Nested
    @DisplayName("POST /api/v1/restaurants")
    class CreateRestaurantEndpointTests {

        @Test
        @DisplayName("Should return 201 when creating restaurant with valid data")
        void shouldReturn201WhenCreatingRestaurantWithValidData() {
            CreateRestaurantRequest request = createValidRequest();

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").isEqualTo("Test Restaurant")
                    .jsonPath("$.status").isEqualTo("PENDING_APPROVAL")
                    .jsonPath("$.isOpen").isEqualTo(false)
                    .jsonPath("$.phone").isEqualTo("11999999999")
                    .jsonPath("$.email").isEqualTo("test@restaurant.com");
        }

        @Test
        @DisplayName("Should return 400 when name is missing")
        void shouldReturn400WhenNameIsMissing() {
            CreateRestaurantRequest request = createValidRequest();
            request.setName(null);

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.fieldErrors.name").exists();
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() {
            CreateRestaurantRequest request = createValidRequest();
            request.setName("");

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 when ownerId is missing")
        void shouldReturn400WhenOwnerIdIsMissing() {
            CreateRestaurantRequest request = createValidRequest();
            request.setOwnerId(null);

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 when email is invalid")
        void shouldReturn400WhenEmailIsInvalid() {
            CreateRestaurantRequest request = createValidRequest();
            request.setEmail("invalid-email");

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 when state has invalid length")
        void shouldReturn400WhenStateHasInvalidLength() {
            CreateRestaurantRequest request = createValidRequest();
            request.setAddressState("São Paulo"); // Should be 2 chars

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    // ==================== READ TESTS ====================

    @Nested
    @DisplayName("GET /api/v1/restaurants")
    class GetRestaurantEndpointTests {

        @Test
        @DisplayName("Should return 200 and restaurant when found")
        void shouldReturn200AndRestaurantWhenFound() {
            // Create restaurant first
            RestaurantResponse created = createRestaurant(createValidRequest());

            webTestClient.get()
                    .uri(BASE_URL + "/{id}", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(created.getId().toString())
                    .jsonPath("$.name").isEqualTo("Test Restaurant");
        }

        @Test
        @DisplayName("Should return 404 when restaurant not found")
        void shouldReturn404WhenRestaurantNotFound() {
            UUID nonExistentId = UUID.randomUUID();

            webTestClient.get()
                    .uri(BASE_URL + "/{id}", nonExistentId)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(404)
                    .jsonPath("$.message").exists();
        }

        @Test
        @DisplayName("Should return 200 and list of restaurants")
        void shouldReturn200AndListOfRestaurants() {
            // Create some restaurants
            createRestaurant(createValidRequest());

            webTestClient.get()
                    .uri(BASE_URL)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(RestaurantResponse.class)
                    .hasSize(1);
        }

        @Test
        @DisplayName("Should return restaurants by owner")
        void shouldReturnRestaurantsByOwner() {
            CreateRestaurantRequest request = createValidRequest();
            RestaurantResponse created = createRestaurant(request);

            webTestClient.get()
                    .uri(BASE_URL + "/owner/{ownerId}", request.getOwnerId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(RestaurantResponse.class)
                    .hasSize(1);
        }
    }

    // ==================== UPDATE TESTS ====================

    @Nested
    @DisplayName("PUT /api/v1/restaurants/{id}")
    class UpdateRestaurantEndpointTests {

        @Test
        @DisplayName("Should return 200 when updating restaurant")
        void shouldReturn200WhenUpdatingRestaurant() {
            RestaurantResponse created = createRestaurant(createValidRequest());

            UpdateRestaurantRequest updateRequest = UpdateRestaurantRequest.builder()
                    .name("Updated Restaurant Name")
                    .description("Updated Description")
                    .build();

            webTestClient.put()
                    .uri(BASE_URL + "/{id}", created.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateRequest)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.name").isEqualTo("Updated Restaurant Name")
                    .jsonPath("$.description").isEqualTo("Updated Description");
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent restaurant")
        void shouldReturn404WhenUpdatingNonExistentRestaurant() {
            UUID nonExistentId = UUID.randomUUID();

            UpdateRestaurantRequest updateRequest = UpdateRestaurantRequest.builder()
                    .name("Updated Name")
                    .build();

            webTestClient.put()
                    .uri(BASE_URL + "/{id}", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateRequest)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    // ==================== STATUS OPERATIONS TESTS ====================

    @Nested
    @DisplayName("PATCH /api/v1/restaurants/{id}/*")
    class StatusOperationsEndpointTests {

        @Test
        @DisplayName("Should open restaurant")
        void shouldOpenRestaurant() {
            RestaurantResponse created = createRestaurant(createValidRequest());

            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/open", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.isOpen").isEqualTo(true);
        }

        @Test
        @DisplayName("Should close restaurant")
        void shouldCloseRestaurant() {
            RestaurantResponse created = createRestaurant(createValidRequest());

            // Open first
            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/open", created.getId())
                    .exchange()
                    .expectStatus().isOk();

            // Then close
            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/close", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.isOpen").isEqualTo(false);
        }

        @Test
        @DisplayName("Should activate restaurant")
        void shouldActivateRestaurant() {
            RestaurantResponse created = createRestaurant(createValidRequest());

            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/activate", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("Should suspend restaurant")
        void shouldSuspendRestaurant() {
            RestaurantResponse created = createRestaurant(createValidRequest());

            // Activate first
            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/activate", created.getId())
                    .exchange()
                    .expectStatus().isOk();

            // Then suspend
            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/suspend", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("SUSPENDED");
        }

        @Test
        @DisplayName("Should pause orders")
        void shouldPauseOrders() {
            RestaurantResponse created = createRestaurant(createValidRequest());

            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/pause-orders", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.isAcceptingOrders").isEqualTo(false);
        }

        @Test
        @DisplayName("Should resume orders")
        void shouldResumeOrders() {
            RestaurantResponse created = createRestaurant(createValidRequest());

            // Pause first
            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/pause-orders", created.getId())
                    .exchange()
                    .expectStatus().isOk();

            // Then resume
            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/resume-orders", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.isAcceptingOrders").isEqualTo(true);
        }
    }

    // ==================== DELETE TESTS ====================

    @Nested
    @DisplayName("DELETE /api/v1/restaurants/{id}")
    class DeleteRestaurantEndpointTests {

        @Test
        @DisplayName("Should return 204 when deleting restaurant")
        void shouldReturn204WhenDeletingRestaurant() {
            RestaurantResponse created = createRestaurant(createValidRequest());

            webTestClient.delete()
                    .uri(BASE_URL + "/{id}", created.getId())
                    .exchange()
                    .expectStatus().isNoContent();

            // Verify it's deleted
            webTestClient.get()
                    .uri(BASE_URL + "/{id}", created.getId())
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent restaurant")
        void shouldReturn404WhenDeletingNonExistentRestaurant() {
            UUID nonExistentId = UUID.randomUUID();

            webTestClient.delete()
                    .uri(BASE_URL + "/{id}", nonExistentId)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    // ==================== SEARCH TESTS ====================

    @Nested
    @DisplayName("GET /api/v1/restaurants/search")
    class SearchRestaurantsEndpointTests {

        @Test
        @DisplayName("Should search restaurants by name")
        void shouldSearchRestaurantsByName() {
            CreateRestaurantRequest request = createValidRequest();
            request.setName("UniqueSearchableName123");
            createRestaurant(request);

            webTestClient.get()
                    .uri(BASE_URL + "/search?q=UniqueSearchable")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(RestaurantResponse.class)
                    .hasSize(1);
        }
    }
}