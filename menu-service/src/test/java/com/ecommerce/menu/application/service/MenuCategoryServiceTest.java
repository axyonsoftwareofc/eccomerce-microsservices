// menu-service/src/test/java/com/ecommerce/menu/application/service/MenuCategoryServiceTest.java
package com.ecommerce.menu.application.service;

import com.ecommerce.menu.application.dto.request.CreateCategoryRequest;
import com.ecommerce.menu.application.dto.response.MenuCategoryResponse;
import com.ecommerce.menu.domain.exception.CategoryNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalTime;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("MenuCategoryService Tests")
class MenuCategoryServiceTest {

    @Autowired
    private MenuCategoryService categoryService;

    private CreateCategoryRequest createValidRequest() {
        return CreateCategoryRequest.builder()
                .restaurantId(UUID.randomUUID())
                .name("Pizzas")
                .description("Nossas deliciosas pizzas")
                .imageUrl("https://example.com/pizzas.jpg")
                .displayOrder(1)
                .availableFrom(LocalTime.of(18, 0))
                .availableUntil(LocalTime.of(23, 0))
                .build();
    }

    @Nested
    @DisplayName("Create Category")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create category with valid data")
        void shouldCreateCategoryWithValidData() {
            CreateCategoryRequest request = createValidRequest();

            StepVerifier.create(categoryService.createCategory(request))
                    .assertNext(response -> {
                        assert response.getId() != null;
                        assert response.getName().equals("Pizzas");
                        assert response.getIsActive() == true;
                        assert response.getDisplayOrder() == 1;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should create category with minimal data")
        void shouldCreateCategoryWithMinimalData() {
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                    .restaurantId(UUID.randomUUID())
                    .name("Bebidas")
                    .build();

            StepVerifier.create(categoryService.createCategory(request))
                    .assertNext(response -> {
                        assert response.getId() != null;
                        assert response.getName().equals("Bebidas");
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Get Category")
    class GetCategoryTests {

        @Test
        @DisplayName("Should get category by ID")
        void shouldGetCategoryById() {
            CreateCategoryRequest request = createValidRequest();

            StepVerifier.create(
                            categoryService.createCategory(request)
                                    .flatMap(created -> categoryService.getCategoryById(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getName().equals("Pizzas");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            UUID nonExistentId = UUID.randomUUID();

            StepVerifier.create(categoryService.getCategoryById(nonExistentId))
                    .expectError(CategoryNotFoundException.class)
                    .verify();
        }

        @Test
        @DisplayName("Should get categories by restaurant")
        void shouldGetCategoriesByRestaurant() {
            UUID restaurantId = UUID.randomUUID();

            CreateCategoryRequest request1 = createValidRequest();
            request1.setRestaurantId(restaurantId);
            request1.setName("Entradas");

            CreateCategoryRequest request2 = createValidRequest();
            request2.setRestaurantId(restaurantId);
            request2.setName("Pratos Principais");

            StepVerifier.create(
                            categoryService.createCategory(request1)
                                    .then(categoryService.createCategory(request2))
                                    .thenMany(categoryService.getCategoriesByRestaurant(restaurantId))
                                    .collectList()
                    )
                    .assertNext(categories -> {
                        assert categories.size() >= 2;
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Update Category")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update category")
        void shouldUpdateCategory() {
            CreateCategoryRequest createRequest = createValidRequest();
            CreateCategoryRequest updateRequest = CreateCategoryRequest.builder()
                    .restaurantId(createRequest.getRestaurantId())
                    .name("Pizzas Especiais")
                    .description("Pizzas gourmet")
                    .build();

            StepVerifier.create(
                            categoryService.createCategory(createRequest)
                                    .flatMap(created ->
                                            categoryService.updateCategory(created.getId(), updateRequest))
                    )
                    .assertNext(response -> {
                        assert response.getName().equals("Pizzas Especiais");
                        assert response.getDescription().equals("Pizzas gourmet");
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Category Status")
    class CategoryStatusTests {

        @Test
        @DisplayName("Should activate category")
        void shouldActivateCategory() {
            CreateCategoryRequest request = createValidRequest();

            StepVerifier.create(
                            categoryService.createCategory(request)
                                    .flatMap(created -> categoryService.deactivateCategory(created.getId()))
                                    .flatMap(deactivated -> categoryService.activateCategory(deactivated.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getIsActive() == true;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should deactivate category")
        void shouldDeactivateCategory() {
            CreateCategoryRequest request = createValidRequest();

            StepVerifier.create(
                            categoryService.createCategory(request)
                                    .flatMap(created -> categoryService.deactivateCategory(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getIsActive() == false;
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Delete Category")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delete category")
        void shouldDeleteCategory() {
            CreateCategoryRequest request = createValidRequest();

            StepVerifier.create(
                            categoryService.createCategory(request)
                                    .flatMap(created ->
                                            categoryService.deleteCategory(created.getId())
                                                    .then(categoryService.getCategoryById(created.getId()))
                                    )
                    )
                    .expectError(CategoryNotFoundException.class)
                    .verify();
        }
    }
}