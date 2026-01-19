// infrastructure/controller/CategoryController.java
package com.ecommerce.restaurant.infrastructure.controller;

import com.ecommerce.restaurant.application.service.CategoryService;
import com.ecommerce.restaurant.domain.entity.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Restaurant category APIs")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all active categories")
    public Flux<Category> getAllCategories() {
        return categoryService.getAllActiveCategories();
    }
}