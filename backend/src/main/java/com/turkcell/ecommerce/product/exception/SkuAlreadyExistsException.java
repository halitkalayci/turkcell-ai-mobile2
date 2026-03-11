package com.turkcell.ecommerce.product.exception;

public class SkuAlreadyExistsException extends RuntimeException {

    public SkuAlreadyExistsException(String sku) {
        super("Bu SKU zaten kayıtlı — sku: " + sku);
    }
}
