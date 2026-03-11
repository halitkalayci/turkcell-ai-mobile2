package com.turkcell.ecommerce.category.exception;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(UUID id) {
        super("Kategori bulunamadı — id: " + id);
    }
}
