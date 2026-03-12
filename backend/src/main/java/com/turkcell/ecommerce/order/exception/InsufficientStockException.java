package com.turkcell.ecommerce.order.exception;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(UUID productId, int requested, int available) {
        super("Yetersiz stok — ürün: " + productId
                + ", istenen: " + requested
                + ", mevcut: " + available);
    }
}
