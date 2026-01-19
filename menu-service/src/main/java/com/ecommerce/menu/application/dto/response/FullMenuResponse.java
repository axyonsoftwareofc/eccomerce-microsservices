// application/dto/response/FullMenuResponse.java
package com.ecommerce.menu.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullMenuResponse {

    private UUID restaurantId;
    private String restaurantName;
    private List<MenuCategoryResponse> categories;
    private Integer totalItems;
}