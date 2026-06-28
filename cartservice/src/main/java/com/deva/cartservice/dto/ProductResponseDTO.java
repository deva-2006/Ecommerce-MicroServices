package com.deva.cartservice.dto;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private String productId;
    private String name;
    private String description;
    private String category;
    private Double price;
}