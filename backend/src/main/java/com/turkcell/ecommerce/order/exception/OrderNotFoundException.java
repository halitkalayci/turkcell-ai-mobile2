package com.turkcell.ecommerce.order.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(UUID id) {
        super("Sipariş bulunamadı — id: " + id);
    }
}
