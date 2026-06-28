package com.deva.orderservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequestDTO {
    private String orderId;
    private String userId;
    private Double amount;
    private String paymentMethod;
}