package com.deva.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequestDTO {
//items come from cart
    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "shippingAddress is required")
    private String shippingAddress;

    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;
}