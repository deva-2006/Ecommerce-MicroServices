package com.deva.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryRequestDTO {

    @NotBlank(message = "productId is required")
    private String productId;

    @NotNull(message = "quantity is required")
    @Min(value = 0, message = "quantity cannot be negative")
    private Integer quantity;
}