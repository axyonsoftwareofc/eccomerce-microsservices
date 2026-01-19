package com.ecommerce.order.application.dto.request;

import com.ecommerce.order.domain.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;

    // Para CONFIRMED
    private Integer estimatedDeliveryTime;

    // Para CANCELLED
    private String cancellationReason;
}