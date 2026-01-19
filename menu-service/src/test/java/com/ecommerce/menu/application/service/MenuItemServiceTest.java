// menu-service/src/test/java/com/ecommerce/menu/application/service/MenuItemServiceTest.java
package com.ecommerce.menu.application.service;

import com.ecommerce.menu.application.dto.request.CreateAddonRequest;
import com.ecommerce.menu.application.dto.request.CreateCategoryRequest;
import com.ecommerce.menu.application.dto.request.CreateMenuItemRequest;
import com.ecommerce.menu.application.dto.request.CreateVariantRequest;
import com.ecommerce.menu.application.dto.request.UpdateMenuItemRequest;
import com.ecommerce.menu.application.dto.response.MenuCategoryResponse;
import com.ecommerce.menu.application.dto.response.MenuItemResponse;
import com.ecommerce.menu.domain.entity.VariantType;
import com.ecommerce.menu.domain.exception.MenuItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("MenuItemService Tests")
class MenuItemServiceTest {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private MenuCategoryService categoryService;

    private UUID restaurantId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();

        // Create a category for the tests
        CreateCategoryRequest categoryRequest = CreateCategoryRequest.builder()
                .restaurantId(restaurantId)
                .name("Test Category " + UUID.randomUUID())
                .build();

        MenuCategoryResponse category = categoryService.createCategory(categoryRequest).block();
        categoryId = category.getId();
    }

    private CreateMenuItemRequest createValidRequest() {
        return CreateMenuItemRequest.builder()
                .restaurantId(restaurantId)
                .categoryId(categoryId)
                .name("Pizza Margherita")
                .description("Pizza tradicional italiana")
                .imageUrl("https://example.com/margherita.jpg")
                .price(new BigDecimal("45.90"))
                .originalPrice(new BigDecimal("55.90"))
                .discountPercentage(new BigDecimal("18"))
                .preparationTime(25)
                .serves(2)
                .calories(800)
                .isVegetarian(true)
                .isVegan(false)
                .isGlutenFree(false)
                .isSpicy(false)
                .isAvailable(true)
                .isFeatured(false)
                .availableFrom(LocalTime.of(18, 0))
                .availableUntil(LocalTime.of(23, 0))
                .stockQuantity(50)
                .maxQuantityPerOrder(5)
                .displayOrder(1)
                .build();
    }

    private CreateMenuItemRequest createRequestWithVariantsAndAddons() {
        List<CreateVariantRequest> variants = List.of(
                CreateVariantRequest.builder()
                        .name("Pequena")
                        .variantType(VariantType.SIZE)
                        .price(new BigDecimal("35.90"))
                        .serves(1)
                        .isDefault(false)
                        .build(),
                CreateVariantRequest.builder()
                        .name("Grande")
                        .variantType(VariantType.SIZE)
                        .price(new BigDecimal("55.90"))
                        .serves(3)
                        .isDefault(true)
                        .build()
        );

        List<CreateAddonRequest> addons = List.of(
                CreateAddonRequest.builder()
                        .name("Bacon Extra")
                        .description("Porção extra de bacon")
                        .price(new BigDecimal("8.00"))
                        .maxQuantity(2)
                        .isRequired(false)
                        .build(),
                CreateAddonRequest.builder()
                        .name("Borda Recheada")
                        .description("Borda recheada com catupiry")
                        .price(new BigDecimal("12.00"))
                        .maxQuantity(1)
                        .isRequired(false)
                        .build()
        );

        CreateMenuItemRequest request = createValidRequest();
        request.setVariants(variants);
        request.setAddons(addons);
        return request;
    }

    @Nested
    @DisplayName("Create Menu Item")
    class CreateMenuItemTests {

        @Test
        @DisplayName("Should create menu item with valid data")
        void shouldCreateMenuItemWithValidData() {
            CreateMenuItemRequest request = createValidRequest();

            StepVerifier.create(menuItemService.createMenuItem(request))
                    .assertNext(response -> {
                        assert response.getId() != null;
                        assert response.getName().equals("Pizza Margherita");
                        assert response.getPrice().compareTo(new BigDecimal("45.90")) == 0;
                        assert response.getIsAvailable() == true;
                        assert response.getIsVegetarian() == true;
                        assert response.getHasDiscount() == true;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should create menu item with variants and addons")
        void shouldCreateMenuItemWithVariantsAndAddons() {
            CreateMenuItemRequest request = createRequestWithVariantsAndAddons();

            StepVerifier.create(menuItemService.createMenuItem(request))
                    .assertNext(response -> {
                        assert response.getId() != null;
                        assert response.getVariants() != null;
                        assert response.getVariants().size() == 2;
                        assert response.getAddons() != null;
                        assert response.getAddons().size() == 2;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should calculate final price with discount")
        void shouldCalculateFinalPriceWithDiscount() {
            CreateMenuItemRequest request = createValidRequest();
            request.setPrice(new BigDecimal("100.00"));
            request.setDiscountPercentage(new BigDecimal("20"));

            StepVerifier.create(menuItemService.createMenuItem(request))
                    .assertNext(response -> {
                        assert response.getFinalPrice().compareTo(new BigDecimal("80.00")) == 0;
                        assert response.getHasDiscount() == true;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should create menu item with minimal data")
        void shouldCreateMenuItemWithMinimalData() {
            CreateMenuItemRequest request = CreateMenuItemRequest.builder()
                    .restaurantId(restaurantId)
                    .categoryId(categoryId)
                    .name("Simple Item")
                    .price(new BigDecimal("19.90"))
                    .build();

            StepVerifier.create(menuItemService.createMenuItem(request))
                    .assertNext(response -> {
                        assert response.getId() != null;
                        assert response.getName().equals("Simple Item");
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Get Menu Item")
    class GetMenuItemTests {

        @Test
        @DisplayName("Should get menu item by ID")
        void shouldGetMenuItemById() {
            CreateMenuItemRequest request = createValidRequest();

            StepVerifier.create(
                            menuItemService.createMenuItem(request)
                                    .flatMap(created -> menuItemService.getMenuItemById(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getName().equals("Pizza Margherita");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should throw exception when menu item not found")
        void shouldThrowExceptionWhenMenuItemNotFound() {
            UUID nonExistentId = UUID.randomUUID();

            StepVerifier.create(menuItemService.getMenuItemById(nonExistentId))
                    .expectError(MenuItemNotFoundException.class)
                    .verify();
        }

        @Test
        @DisplayName("Should get menu items by restaurant")
        void shouldGetMenuItemsByRestaurant() {
            CreateMenuItemRequest request1 = createValidRequest();
            request1.setName("Item 1");

            CreateMenuItemRequest request2 = createValidRequest();
            request2.setName("Item 2");

            StepVerifier.create(
                            menuItemService.createMenuItem(request1)
                                    .then(menuItemService.createMenuItem(request2))
                                    .thenMany(menuItemService.getMenuItemsByRestaurant(restaurantId))
                                    .collectList()
                    )
                    .assertNext(items -> {
                        assert items.size() >= 2;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should get menu items by category")
        void shouldGetMenuItemsByCategory() {
            CreateMenuItemRequest request = createValidRequest();

            StepVerifier.create(
                            menuItemService.createMenuItem(request)
                                    .thenMany(menuItemService.getMenuItemsByCategory(categoryId))
                                    .collectList()
                    )
                    .assertNext(items -> {
                        assert items.size() >= 1;
                        assert items.stream().allMatch(i -> i.getCategoryId().equals(categoryId));
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should get available items")
        void shouldGetAvailableItems() {
            CreateMenuItemRequest request = createValidRequest();
            request.setIsAvailable(true);

            StepVerifier.create(
                            menuItemService.createMenuItem(request)
                                    .thenMany(menuItemService.getAvailableItems(restaurantId))
                                    .collectList()
                    )
                    .assertNext(items -> {
                        assert items.stream().allMatch(i -> i.getIsAvailable());
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should get featured items")
        void shouldGetFeaturedItems() {
            CreateMenuItemRequest request = createValidRequest();
            request.setIsFeatured(true);
            request.setName("Featured Item");

            StepVerifier.create(
                            menuItemService.createMenuItem(request)
                                    .thenMany(menuItemService.getFeaturedItems(restaurantId))
                                    .collectList()
                    )
                    .assertNext(items -> {
                        assert items.stream().allMatch(i -> i.getIsFeatured());
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Update Menu Item")
    class UpdateMenuItemTests {

        @Test
        @DisplayName("Should update menu item")
        void shouldUpdateMenuItem() {
            CreateMenuItemRequest createRequest = createValidRequest();
            UpdateMenuItemRequest updateRequest = UpdateMenuItemRequest.builder()
                    .name("Pizza Margherita Especial")
                    .price(new BigDecimal("49.90"))
                    .description("Nova descrição")
                    .build();

            StepVerifier.create(
                            menuItemService.createMenuItem(createRequest)
                                    .flatMap(created ->
                                            menuItemService.updateMenuItem(created.getId(), updateRequest))
                    )
                    .assertNext(response -> {
                        assert response.getName().equals("Pizza Margherita Especial");
                        assert response.getPrice().compareTo(new BigDecimal("49.90")) == 0;
                        assert response.getDescription().equals("Nova descrição");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent item")
        void shouldThrowExceptionWhenUpdatingNonExistentItem() {
            UUID nonExistentId = UUID.randomUUID();
            UpdateMenuItemRequest updateRequest = UpdateMenuItemRequest.builder()
                    .name("Updated Name")
                    .build();

            StepVerifier.create(menuItemService.updateMenuItem(nonExistentId, updateRequest))
                    .expectError(MenuItemNotFoundException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Menu Item Availability")
    class MenuItemAvailabilityTests {

        @Test
        @DisplayName("Should mark item as available")
        void shouldMarkItemAsAvailable() {
            CreateMenuItemRequest request = createValidRequest();
            request.setIsAvailable(false);

            StepVerifier.create(
                            menuItemService.createMenuItem(request)
                                    .flatMap(created -> menuItemService.markAsAvailable(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getIsAvailable() == true;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should mark item as unavailable")
        void shouldMarkItemAsUnavailable() {
            CreateMenuItemRequest request = createValidRequest();

            StepVerifier.create(
                            menuItemService.createMenuItem(request)
                                    .flatMap(created -> menuItemService.markAsUnavailable(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getIsAvailable() == false;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should feature item")
        void shouldFeatureItem() {
            CreateMenuItemRequest request = createValidRequest();

            StepVerifier.create(
                            menuItemService.createMenuItem(request)
                                    .flatMap(created -> menuItemService.featureItem(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getIsFeatured() == true;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should unfeature item")
        void shouldUnfeatureItem() {
            CreateMenuItemRequest request = createValidRequest();
            request.setIsFeatured(true);

            StepVerifier.create(
                            menuItemService.createMenuItem(request)
                                    .flatMap(created -> menuItemService.unfeatureItem(created.getId()))
                    )
                    .assertNext(response -> {
                        assert response.getIsFeatured() == false;
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Delete Menu Item")
    class DeleteMenuItemTests {

        @Test
        @DisplayName("Should delete menu item")
        void shouldDeleteMenuItem() {
            CreateMenuItemRequest request = createValidRequest();

            StepVerifier.create(
                            menuItemService.createMenuItem(request)
                                    .flatMap(created ->
                                            menuItemService.deleteMenuItem(created.getId())
                                                    .then(menuItemService.getMenuItemById(created.getId()))
                                    )
                    )
                    .expectError(MenuItemNotFoundException.class)
                    .verify();
        }

        @Test
        @DisplayName("Should delete menu item with variants and addons")
        void shouldDeleteMenuItemWithVariantsAndAddons() {
            CreateMenuItemRequest request = createRequestWithVariantsAndAddons();

            StepVerifier.create(
                            menuItemService.createMenuItem(request)
                                    .flatMap(created ->
                                            menuItemService.deleteMenuItem(created.getId())
                                                    .then(menuItemService.getMenuItemById(created.getId()))
                                    )
                    )
                    .expectError(MenuItemNotFoundException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Search Menu Items")
    class SearchMenuItemTests {

        @Test
        @DisplayName("Should search items by name")
        void shouldSearchItemsByName() {
            CreateMenuItemRequest request = createValidRequest();
            request.setName("UniqueSearchableItem12345");

            StepVerifier.create(
                            menuItemService.createMenuItem(request)
                                    .thenMany(menuItemService.searchItems(restaurantId, "UniqueSearchable"))
                                    .collectList()
                    )
                    .assertNext(items -> {
                        assert items.stream().anyMatch(i -> i.getName().contains("UniqueSearchable"));
                    })
                    .verifyComplete();
        }
    }
}