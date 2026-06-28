package com.deva.orderservice.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private String userId;
    private String productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
    private String addedAt;
}