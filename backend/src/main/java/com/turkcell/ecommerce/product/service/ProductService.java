package com.turkcell.ecommerce.product.service;

import com.turkcell.ecommerce.product.dto.ProductRequest;
import com.turkcell.ecommerce.product.dto.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(UUID id);

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(UUID id, ProductRequest request);

    void deleteProduct(UUID id);
}
