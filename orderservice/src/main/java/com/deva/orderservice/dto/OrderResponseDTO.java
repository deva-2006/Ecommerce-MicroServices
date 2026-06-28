package com.deva.orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    private String orderId;
    private String paymentId;
    private String userId;
    private List<OrderItemDTO> items;
    private Double totalAmount;
    private String status;
    private String shippingAddress;
    private String createdAt;
}