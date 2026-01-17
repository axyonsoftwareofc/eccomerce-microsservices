// domain/entity/Category.java
package com.ecommerce.restaurant.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("restaurant_categories")
public class Category {

    @Id
    private UUID id;

    @Column("name")
    private String name; // Pizzaria, Hamburgueria, Japonês, Italiana, etc.

    @Column("icon_url")
    private String iconUrl;

    @Column("is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column("display_order")
    private Integer displayOrder;
}