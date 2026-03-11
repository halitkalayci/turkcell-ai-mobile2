package com.turkcell.ecommerce.category.exception;

public class CategoryNameAlreadyExistsException extends RuntimeException {

    public CategoryNameAlreadyExistsException(String name) {
        super("Kategori adı zaten kayıtlı — name: " + name);
    }
}
