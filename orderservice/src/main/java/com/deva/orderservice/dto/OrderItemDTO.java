package com.deva.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemDTO {

    @NotBlank(message = "productId is required")
    private String productId;

    @NotBlank(message = "productName is required")
    private String productName;

    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be positive")
    private Integer quantity;

    @NotNull(message = "price is required")
    @Positive(message = "price must be positive")
    private Double price;
}