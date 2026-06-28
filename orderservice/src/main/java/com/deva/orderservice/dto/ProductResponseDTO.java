package com.deva.orderservice.dto;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private String productId;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Integer stock;
}