package com.deva.inventoryservice.service;

import com.deva.inventoryservice.dto.InventoryRequestDTO;
import com.deva.inventoryservice.dto.InventoryResponseDTO;

import java.util.List;

public interface InventoryService {
    InventoryResponseDTO createInventory(InventoryRequestDTO request);
    InventoryResponseDTO getInventoryByProductId(String productId);
    List<InventoryResponseDTO> getAllInventory();
    InventoryResponseDTO addStock(String productId, Integer quantity);
    InventoryResponseDTO updateStock(String productId, Integer quantity);
    void deductStock(String productId, Integer quantity);
    void validateStock(String productId, Integer quantity);
    void deleteInventory(String productId);
}