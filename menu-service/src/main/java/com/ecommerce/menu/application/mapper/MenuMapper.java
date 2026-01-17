package com.ecommerce.menu.application.mapper;

import com.ecommerce.menu.application.dto.request.CreateMenuRequest;
import com.ecommerce.menu.application.dto.response.MenuResponse;
import com.ecommerce.menu.domain.entity.Menu;
import org.springframework.stereotype.Component;

@Component
public class MenuMapper {

    public Menu toEntity(CreateMenuRequest request) {
        return Menu.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .categoryId(request.getCategoryId())
                .imageUrl(request.getImageUrl())
                .isActive(true)
                .build();
    }

    public MenuResponse toResponse(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .description(menu.getDescription())
                .price(menu.getPrice())
                .sku(menu.getSku())
                .categoryId(menu.getCategoryId())
                .isActive(menu.getIsActive())
                .imageUrl(menu.getImageUrl())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }
}