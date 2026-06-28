package com.deva.inventoryservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponseDTO {
    private String productId;
    private Integer quantity;
    private String updatedAt;
}