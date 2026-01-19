package com.ecommerce.menu.application.mapper;

import com.ecommerce.menu.application.dto.request.CreateAddonRequest;
import com.ecommerce.menu.application.dto.request.CreateCategoryRequest;
import com.ecommerce.menu.application.dto.request.CreateMenuItemRequest;
import com.ecommerce.menu.application.dto.request.CreateVariantRequest;
import com.ecommerce.menu.application.dto.response.AddonResponse;
import com.ecommerce.menu.application.dto.response.MenuCategoryResponse;
import com.ecommerce.menu.application.dto.response.MenuItemResponse;
import com.ecommerce.menu.application.dto.response.VariantResponse;
import com.ecommerce.menu.domain.entity.MenuCategory;
import com.ecommerce.menu.domain.entity.MenuItem;
import com.ecommerce.menu.domain.entity.MenuItemAddon;
import com.ecommerce.menu.domain.entity.MenuItemVariant;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class MenuMapper {

    // ========== MENU ITEM ==========

    public MenuItem toEntity(CreateMenuItemRequest request) {
        return MenuItem.builder()
                .restaurantId(request.getRestaurantId())
                .categoryId(request.getCategoryId())
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .discountPercentage(request.getDiscountPercentage())
                .preparationTime(request.getPreparationTime())
                .serves(request.getServes())
                .calories(request.getCalories())
                .isVegetarian(request.getIsVegetarian() != null ? request.getIsVegetarian() : false)
                .isVegan(request.getIsVegan() != null ? request.getIsVegan() : false)
                .isGlutenFree(request.getIsGlutenFree() != null ? request.getIsGlutenFree() : false)
                .isSpicy(request.getIsSpicy() != null ? request.getIsSpicy() : false)
                .spicyLevel(request.getSpicyLevel())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .availableFrom(request.getAvailableFrom())
                .availableUntil(request.getAvailableUntil())
                .stockQuantity(request.getStockQuantity())
                .maxQuantityPerOrder(request.getMaxQuantityPerOrder())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();
    }

    public MenuItemResponse toResponse(MenuItem item) {
        return toResponseWithDetails(item, null, null, null);
    }

    public MenuItemResponse toResponseWithDetails(MenuItem item, String categoryName,
                                                  List<VariantResponse> variants,
                                                  List<AddonResponse> addons) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .restaurantId(item.getRestaurantId())
                .categoryId(item.getCategoryId())
                .categoryName(categoryName)
                .name(item.getName())
                .description(item.getDescription())
                .imageUrl(item.getImageUrl())
                .price(item.getPrice())
                .originalPrice(item.getOriginalPrice())
                .finalPrice(item.getFinalPrice())
                .discountPercentage(item.getDiscountPercentage())
                .hasDiscount(item.hasDiscount())
                .preparationTime(item.getPreparationTime())
                .serves(item.getServes())
                .calories(item.getCalories())
                .isVegetarian(item.getIsVegetarian())
                .isVegan(item.getIsVegan())
                .isGlutenFree(item.getIsGlutenFree())
                .isSpicy(item.getIsSpicy())
                .spicyLevel(item.getSpicyLevel())
                .isAvailable(item.getIsAvailable())
                .isCurrentlyAvailable(item.isCurrentlyAvailable())
                .isFeatured(item.getIsFeatured())
                .isBestSeller(item.getIsBestSeller())
                .availableFrom(item.getAvailableFrom())
                .availableUntil(item.getAvailableUntil())
                .stockQuantity(item.getStockQuantity())
                .maxQuantityPerOrder(item.getMaxQuantityPerOrder())
                .displayOrder(item.getDisplayOrder())
                .totalOrders(item.getTotalOrders())
                .variants(variants)
                .addons(addons)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    // ========== CATEGORY ==========

    public MenuCategory toCategoryEntity(CreateCategoryRequest request) {
        return MenuCategory.builder()
                .restaurantId(request.getRestaurantId())
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .availableFrom(request.getAvailableFrom())
                .availableUntil(request.getAvailableUntil())
                .isActive(true)
                .build();
    }

    public MenuCategoryResponse toCategoryResponse(MenuCategory category) {
        return toCategoryResponseWithItems(category, null, 0);
    }

    public MenuCategoryResponse toCategoryResponseWithItems(MenuCategory category,
                                                            List<MenuItemResponse> items,
                                                            int itemCount) {
        return MenuCategoryResponse.builder()
                .id(category.getId())
                .restaurantId(category.getRestaurantId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .isActive(category.getIsActive())
                .isCurrentlyAvailable(category.isCurrentlyAvailable())
                .displayOrder(category.getDisplayOrder())
                .availableFrom(category.getAvailableFrom())
                .availableUntil(category.getAvailableUntil())
                .itemCount(itemCount)
                .items(items)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    // ========== ADDON ==========

    public MenuItemAddon toAddonEntity(CreateAddonRequest request, UUID menuItemId) {
        return MenuItemAddon.builder()
                .menuItemId(menuItemId)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .maxQuantity(request.getMaxQuantity() != null ? request.getMaxQuantity() : 1)
                .isRequired(request.getIsRequired() != null ? request.getIsRequired() : false)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isAvailable(true)
                .build();
    }

    public AddonResponse toAddonResponse(MenuItemAddon addon) {
        return AddonResponse.builder()
                .id(addon.getId())
                .menuItemId(addon.getMenuItemId())
                .name(addon.getName())
                .description(addon.getDescription())
                .price(addon.getPrice())
                .isAvailable(addon.getIsAvailable())
                .maxQuantity(addon.getMaxQuantity())
                .isRequired(addon.getIsRequired())
                .displayOrder(addon.getDisplayOrder())
                .build();
    }

    // ========== VARIANT ==========

    public MenuItemVariant toVariantEntity(CreateVariantRequest request, UUID menuItemId) {
        return MenuItemVariant.builder()
                .menuItemId(menuItemId)
                .name(request.getName())
                .variantType(request.getVariantType())
                .price(request.getPrice())
                .priceModifier(request.getPriceModifier())
                .serves(request.getServes())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isAvailable(true)
                .build();
    }

    public VariantResponse toVariantResponse(MenuItemVariant variant, BigDecimal basePrice) {
        return VariantResponse.builder()
                .id(variant.getId())
                .menuItemId(variant.getMenuItemId())
                .name(variant.getName())
                .variantType(variant.getVariantType())
                .price(variant.getPrice())
                .priceModifier(variant.getPriceModifier())
                .finalPrice(variant.calculateFinalPrice(basePrice))
                .serves(variant.getServes())
                .isDefault(variant.getIsDefault())
                .isAvailable(variant.getIsAvailable())
                .displayOrder(variant.getDisplayOrder())
                .build();
    }
}