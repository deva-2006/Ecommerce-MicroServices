package com.deva.paymentservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponseDTO {
    private String paymentId;
    private String orderId;
    private String userId;
    private Double amount;
    private String paymentMethod;
    private String status;
    private String createdAt;
}