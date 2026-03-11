package com.turkcell.ecommerce.product.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(UUID id) {
        super("Ürün bulunamadı: " + id);
    }
}
