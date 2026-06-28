package com.deva.orderservice.dto;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    private String paymentId;
    private String orderId;
    private String userId;
    private Double amount;
    private String paymentMethod;
    private String status;
    private String createdAt;
}