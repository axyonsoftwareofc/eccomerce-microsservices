// menu-service/src/test/java/com/ecommerce/menu/infrastructure/controller/MenuCategoryControllerTest.java
package com.ecommerce.menu.infrastructure.controller;

import com.ecommerce.menu.application.dto.request.CreateCategoryRequest;
import com.ecommerce.menu.application.dto.response.MenuCategoryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalTime;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("MenuCategoryController Tests")
class MenuCategoryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_URL = "/api/v1/menu-categories";

    private CreateCategoryRequest createValidRequest() {
        return CreateCategoryRequest.builder()
                .restaurantId(UUID.randomUUID())
                .name("Test Category")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .displayOrder(1)
                .availableFrom(LocalTime.of(8, 0))
                .availableUntil(LocalTime.of(22, 0))
                .build();
    }

    private MenuCategoryResponse createCategory(CreateCategoryRequest request) {
        return webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MenuCategoryResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Nested
    @DisplayName("POST /api/v1/menu-categories")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should return 201 when creating category")
        void shouldReturn201WhenCreatingCategory() {
            CreateCategoryRequest request = createValidRequest();

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").isEqualTo("Test Category")
                    .jsonPath("$.isActive").isEqualTo(true);
        }

        @Test
        @DisplayName("Should return 400 when name is missing")
        void shouldReturn400WhenNameIsMissing() {
            CreateCategoryRequest request = createValidRequest();
            request.setName(null);

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/menu-categories")
    class GetCategoryTests {

        @Test
        @DisplayName("Should return category by ID")
        void shouldReturnCategoryById() {
            MenuCategoryResponse created = createCategory(createValidRequest());

            webTestClient.get()
                    .uri(BASE_URL + "/{id}", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(created.getId().toString())
                    .jsonPath("$.name").isEqualTo("Test Category");
        }

        @Test
        @DisplayName("Should return 404 when category not found")
        void shouldReturn404WhenCategoryNotFound() {
            UUID nonExistentId = UUID.randomUUID();

            webTestClient.get()
                    .uri(BASE_URL + "/{id}", nonExistentId)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("Should return categories by restaurant")
        void shouldReturnCategoriesByRestaurant() {
            CreateCategoryRequest request = createValidRequest();
            createCategory(request);

            webTestClient.get()
                    .uri(BASE_URL + "/restaurant/{restaurantId}", request.getRestaurantId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MenuCategoryResponse.class)
                    .hasSize(1);
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/menu-categories/{id}")
    class UpdateCategoryStatusTests {

        @Test
        @DisplayName("Should activate category")
        void shouldActivateCategory() {
            MenuCategoryResponse created = createCategory(createValidRequest());

            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/activate", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.isActive").isEqualTo(true);
        }

        @Test
        @DisplayName("Should deactivate category")
        void shouldDeactivateCategory() {
            MenuCategoryResponse created = createCategory(createValidRequest());

            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/deactivate", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.isActive").isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/menu-categories/{id}")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delete category")
        void shouldDeleteCategory() {
            MenuCategoryResponse created = createCategory(createValidRequest());

            webTestClient.delete()
                    .uri(BASE_URL + "/{id}", created.getId())
                    .exchange()
                    .expectStatus().isNoContent();

            webTestClient.get()
                    .uri(BASE_URL + "/{id}", created.getId())
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }
}