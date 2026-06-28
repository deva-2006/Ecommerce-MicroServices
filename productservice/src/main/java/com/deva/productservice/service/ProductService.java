package com.deva.productservice.service;

import com.deva.productservice.dto.ProductRequestDTO;
import com.deva.productservice.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO request);
    ProductResponseDTO getProductById(String productId);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO updateProduct(String productId, ProductRequestDTO request);
    void deleteProduct(String productId);
}