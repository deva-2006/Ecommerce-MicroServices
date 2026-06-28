package com.deva.inventoryservice.service;

import com.deva.inventoryservice.dto.InventoryRequestDTO;
import com.deva.inventoryservice.dto.InventoryResponseDTO;
import com.deva.inventoryservice.entity.Inventory;
import com.deva.inventoryservice.exception.ResourceNotFoundException;
import com.deva.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public InventoryResponseDTO createInventory(InventoryRequestDTO request) {
        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .updatedAt(LocalDateTime.now().toString())
                .build();
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponseDTO getInventoryByProductId(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for productId: " + productId));
        return toResponse(inventory);
    }

    @Override
    public List<InventoryResponseDTO> getAllInventory() {
        return inventoryRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList()); //It converts each Inventory entity to an InventoryResponseDTO using the toResponse method and collects them into a list.
    }

    @Override
    public InventoryResponseDTO addStock(String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for productId: " + productId));
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setUpdatedAt(LocalDateTime.now().toString());
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponseDTO updateStock(String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for productId: " + productId));
        inventory.setQuantity(quantity);
        inventory.setUpdatedAt(LocalDateTime.now().toString());
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public void deductStock(String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for productId: " + productId));
        if (inventory.getQuantity() < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock for productId: " + productId
                            + ". Available: " + inventory.getQuantity()
                            + ", Requested: " + quantity);
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setUpdatedAt(LocalDateTime.now().toString());
        inventoryRepository.save(inventory);
    }

    @Override
    public void validateStock(String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for productId: " + productId));
        if (inventory.getQuantity() < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock for productId: " + productId
                            + ". Available: " + inventory.getQuantity()
                            + ", Requested: " + quantity);
        }
    }

    @Override
    public void deleteInventory(String productId) {
        inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for productId: " + productId));
        inventoryRepository.deleteByProductId(productId);
    }

    private InventoryResponseDTO toResponse(Inventory inventory) {
        return InventoryResponseDTO.builder()
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}