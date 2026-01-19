package com.ecommerce.order.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Restaurant ID is required")
    private UUID restaurantId;

    // Endereço de entrega
    @NotBlank(message = "Street is required")
    private String deliveryStreet;

    @NotBlank(message = "Number is required")
    private String deliveryNumber;

    private String deliveryComplement;

    @NotBlank(message = "Neighborhood is required")
    private String deliveryNeighborhood;

    @NotBlank(message = "City is required")
    private String deliveryCity;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 2, message = "State must be 2 characters")
    private String deliveryState;

    @NotBlank(message = "ZIP code is required")
    private String deliveryZipCode;

    private BigDecimal deliveryLatitude;
    private BigDecimal deliveryLongitude;

    // Itens do pedido
    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemRequest> items;

    // Opcionais
    private String notes;
    private BigDecimal deliveryFee;
    private String couponCode;
}