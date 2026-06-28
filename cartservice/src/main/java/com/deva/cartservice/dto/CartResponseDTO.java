package com.deva.cartservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponseDTO {
    private String userId;
    private String productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
    private String addedAt;
}