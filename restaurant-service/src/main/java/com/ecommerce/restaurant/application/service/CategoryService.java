// application/service/CategoryService.java
package com.ecommerce.restaurant.application.service;

import com.ecommerce.restaurant.domain.entity.Category;
import com.ecommerce.restaurant.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Flux<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    public Mono<Category> getCategoryById(UUID id) {
        return categoryRepository.findById(id);
    }
}