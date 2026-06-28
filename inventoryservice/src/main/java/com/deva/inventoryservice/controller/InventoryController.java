package com.deva.inventoryservice.controller;

import com.deva.inventoryservice.dto.InventoryRequestDTO;
import com.deva.inventoryservice.dto.InventoryResponseDTO;
import com.deva.inventoryservice.dto.StockDeductRequestDTO;
import com.deva.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponseDTO> createInventory(
            @Valid @RequestBody InventoryRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDTO> getInventory(
            @PathVariable String productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @PutMapping("/{productId}/add-stock")
    public ResponseEntity<InventoryResponseDTO> addStock(
            @PathVariable String productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.addStock(productId, quantity));
    }

    @PutMapping("/{productId}/update-stock")
    public ResponseEntity<InventoryResponseDTO> updateStock(
            @PathVariable String productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.updateStock(productId, quantity));
    }

    // Called by Order Service after payment SUCCESS
    @PutMapping("/{productId}/deduct-stock")
    public ResponseEntity<Void> deductStock(
            @PathVariable String productId,
            @Valid @RequestBody StockDeductRequestDTO request) {
        inventoryService.deductStock(productId, request.getQuantity());
        return ResponseEntity.noContent().build();
    }

    // Called by Order Service during order creation
    @GetMapping("/{productId}/validate")
    public ResponseEntity<Void> validateStock(
            @PathVariable String productId,
            @RequestParam Integer quantity) {
        inventoryService.validateStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable String productId) {
        inventoryService.deleteInventory(productId);
        return ResponseEntity.noContent().build();
    }
}