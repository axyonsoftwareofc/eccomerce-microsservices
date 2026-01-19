package com.ecommerce.order.application.mapper;

import com.ecommerce.order.application.dto.request.CreateOrderRequest;
import com.ecommerce.order.application.dto.request.OrderItemRequest;
import com.ecommerce.order.application.dto.response.OrderItemResponse;
import com.ecommerce.order.application.dto.response.OrderResponse;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class OrderMapper {

    public Order toEntity(CreateOrderRequest request) {
        return Order.builder()
                .customerId(request.getCustomerId())
                .restaurantId(request.getRestaurantId())
                .deliveryStreet(request.getDeliveryStreet())
                .deliveryNumber(request.getDeliveryNumber())
                .deliveryComplement(request.getDeliveryComplement())
                .deliveryNeighborhood(request.getDeliveryNeighborhood())
                .deliveryCity(request.getDeliveryCity())
                .deliveryState(request.getDeliveryState())
                .deliveryZipCode(request.getDeliveryZipCode())
                .deliveryLatitude(request.getDeliveryLatitude())
                .deliveryLongitude(request.getDeliveryLongitude())
                .deliveryFee(request.getDeliveryFee() != null ? request.getDeliveryFee() : BigDecimal.ZERO)
                .notes(request.getNotes())
                .build();
    }

    public OrderItem toOrderItem(OrderItemRequest request, UUID orderId) {
        OrderItem item = OrderItem.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .productId(request.getProductId())
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .notes(request.getNotes())
                .build();
        item.calculateTotalPrice();
        return item;
    }

    public List<OrderItem> toOrderItems(List<OrderItemRequest> requests, UUID orderId) {
        return requests.stream()
                .map(r -> toOrderItem(r, orderId))
                .toList();
    }

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems() != null
                ? order.getItems().stream().map(this::toItemResponse).toList()
                : List.of();

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .status(order.getStatus())
                .deliveryStreet(order.getDeliveryStreet())
                .deliveryNumber(order.getDeliveryNumber())
                .deliveryComplement(order.getDeliveryComplement())
                .deliveryNeighborhood(order.getDeliveryNeighborhood())
                .deliveryCity(order.getDeliveryCity())
                .deliveryState(order.getDeliveryState())
                .deliveryZipCode(order.getDeliveryZipCode())
                .fullDeliveryAddress(order.getFullDeliveryAddress())
                .deliveryLatitude(order.getDeliveryLatitude())
                .deliveryLongitude(order.getDeliveryLongitude())
                .subtotal(order.getSubtotal())
                .deliveryFee(order.getDeliveryFee())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .items(itemResponses)
                .notes(order.getNotes())
                .cancellationReason(order.getCancellationReason())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .confirmedAt(order.getConfirmedAt())
                .preparingAt(order.getPreparingAt())
                .readyAt(order.getReadyAt())
                .pickedUpAt(order.getPickedUpAt())
                .deliveredAt(order.getDeliveredAt())
                .cancelledAt(order.getCancelledAt())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .notes(item.getNotes())
                .build();
    }
}