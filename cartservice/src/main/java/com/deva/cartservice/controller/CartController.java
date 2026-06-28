package com.deva.cartservice.controller;

import com.deva.cartservice.dto.CartRequestDTO;
import com.deva.cartservice.dto.CartResponseDTO;
import com.deva.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    @PostMapping
    public ResponseEntity<CartResponseDTO> addToCart(@Valid @RequestBody CartRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addToCart(request));
    }


    @GetMapping("/{userId}")
    public ResponseEntity<List<CartResponseDTO>> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PutMapping("/{userId}/{productId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(
            @PathVariable String userId,
            @PathVariable String productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable String userId,
            @PathVariable String productId) {
        cartService.deleteCartItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}