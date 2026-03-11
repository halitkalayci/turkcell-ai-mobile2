package com.turkcell.ecommerce.product.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(UUID id) {
        super("Ürün bulunamadı — id: " + id);
    }

    public ProductNotFoundException(String sku) {
        super("Ürün bulunamadı — sku: " + sku);
    }
}
