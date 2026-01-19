// menu-service/src/test/java/com/ecommerce/menu/infrastructure/controller/MenuItemControllerTest.java
package com.ecommerce.menu.infrastructure.controller;

import com.ecommerce.menu.application.dto.request.CreateCategoryRequest;
import com.ecommerce.menu.application.dto.request.CreateMenuItemRequest;
import com.ecommerce.menu.application.dto.request.UpdateMenuItemRequest;
import com.ecommerce.menu.application.dto.response.MenuCategoryResponse;
import com.ecommerce.menu.application.dto.response.MenuItemResponse;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("MenuItemController Tests")
class MenuItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_URL = "/api/v1/menu-items";
    private static final String CATEGORY_URL = "/api/v1/menu-categories";

    private UUID restaurantId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();

        CreateCategoryRequest categoryRequest = CreateCategoryRequest.builder()
                .restaurantId(restaurantId)
                .name("Test Category " + UUID.randomUUID())
                .build();

        MenuCategoryResponse category = webTestClient.post()
                .uri(CATEGORY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(categoryRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MenuCategoryResponse.class)
                .returnResult()
                .getResponseBody();

        categoryId = category.getId();
    }

    private CreateMenuItemRequest createValidRequest() {
        return CreateMenuItemRequest.builder()
                .restaurantId(restaurantId)
                .categoryId(categoryId)
                .name("Test Menu Item")
                .description("Test Description")
                .price(new BigDecimal("29.90"))
                .preparationTime(20)
                .serves(1)
                .isAvailable(true)
                .build();
    }

    private MenuItemResponse createMenuItem(CreateMenuItemRequest request) {
        return webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MenuItemResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Nested
    @DisplayName("POST /api/v1/menu-items")
    class CreateMenuItemTests {

        @Test
        @DisplayName("Should return 201 when creating menu item")
        void shouldReturn201WhenCreatingMenuItem() {
            CreateMenuItemRequest request = createValidRequest();

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").isEqualTo("Test Menu Item")
                    .jsonPath("$.price").isEqualTo(29.90)
                    .jsonPath("$.isAvailable").isEqualTo(true);
        }

        @Test
        @DisplayName("Should return 400 when name is missing")
        void shouldReturn400WhenNameIsMissing() {
            CreateMenuItemRequest request = createValidRequest();
            request.setName(null);

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 when price is missing")
        void shouldReturn400WhenPriceIsMissing() {
            CreateMenuItemRequest request = createValidRequest();
            request.setPrice(null);

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 when price is negative")
        void shouldReturn400WhenPriceIsNegative() {
            CreateMenuItemRequest request = createValidRequest();
            request.setPrice(new BigDecimal("-10.00"));

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/menu-items")
    class GetMenuItemTests {

        @Test
        @DisplayName("Should return menu item by ID")
        void shouldReturnMenuItemById() {
            MenuItemResponse created = createMenuItem(createValidRequest());

            webTestClient.get()
                    .uri(BASE_URL + "/{id}", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(created.getId().toString())
                    .jsonPath("$.name").isEqualTo("Test Menu Item");
        }

        @Test
        @DisplayName("Should return 404 when menu item not found")
        void shouldReturn404WhenMenuItemNotFound() {
            UUID nonExistentId = UUID.randomUUID();

            webTestClient.get()
                    .uri(BASE_URL + "/{id}", nonExistentId)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("Should return menu items by restaurant")
        void shouldReturnMenuItemsByRestaurant() {
            createMenuItem(createValidRequest());

            webTestClient.get()
                    .uri(BASE_URL + "/restaurant/{restaurantId}", restaurantId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MenuItemResponse.class)
                    .hasSize(1);
        }

        @Test
        @DisplayName("Should return menu items by category")
        void shouldReturnMenuItemsByCategory() {
            createMenuItem(createValidRequest());

            webTestClient.get()
                    .uri(BASE_URL + "/category/{categoryId}", categoryId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MenuItemResponse.class)
                    .hasSize(1);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/menu-items/{id}")
    class UpdateMenuItemTests {

        @Test
        @DisplayName("Should update menu item")
        void shouldUpdateMenuItem() {
            MenuItemResponse created = createMenuItem(createValidRequest());

            UpdateMenuItemRequest updateRequest = UpdateMenuItemRequest.builder()
                    .name("Updated Item Name")
                    .price(new BigDecimal("39.90"))
                    .build();

            webTestClient.put()
                    .uri(BASE_URL + "/{id}", created.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateRequest)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.name").isEqualTo("Updated Item Name")
                    .jsonPath("$.price").isEqualTo(39.90);
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/menu-items/{id}")
    class MenuItemStatusTests {

        @Test
        @DisplayName("Should mark item as available")
        void shouldMarkItemAsAvailable() {
            MenuItemResponse created = createMenuItem(createValidRequest());

            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/available", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.isAvailable").isEqualTo(true);
        }

        @Test
        @DisplayName("Should mark item as unavailable")
        void shouldMarkItemAsUnavailable() {
            MenuItemResponse created = createMenuItem(createValidRequest());

            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/unavailable", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.isAvailable").isEqualTo(false);
        }

        @Test
        @DisplayName("Should feature item")
        void shouldFeatureItem() {
            MenuItemResponse created = createMenuItem(createValidRequest());

            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/feature", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.isFeatured").isEqualTo(true);
        }

        @Test
        @DisplayName("Should unfeature item")
        void shouldUnfeatureItem() {
            MenuItemResponse created = createMenuItem(createValidRequest());

            webTestClient.patch()
                    .uri(BASE_URL + "/{id}/unfeature", created.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.isFeatured").isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/menu-items/{id}")
    class DeleteMenuItemTests {

        @Test
        @DisplayName("Should delete menu item")
        void shouldDeleteMenuItem() {
            MenuItemResponse created = createMenuItem(createValidRequest());

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

    @Nested
    @DisplayName("GET /api/v1/menu-items/restaurant/{restaurantId}/search")
    class SearchMenuItemTests {

        @Test
        @DisplayName("Should search items by name")
        void shouldSearchItemsByName() {
            CreateMenuItemRequest request = createValidRequest();
            request.setName("UniqueSearchablePizza12345");
            createMenuItem(request);

            webTestClient.get()
                    .uri(BASE_URL + "/restaurant/{restaurantId}/search?q=UniqueSearchable", restaurantId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MenuItemResponse.class)
                    .hasSize(1);
        }
    }
}