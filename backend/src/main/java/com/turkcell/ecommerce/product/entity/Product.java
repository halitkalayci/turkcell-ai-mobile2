package com.turkcell.ecommerce.product.entity;

import com.turkcell.ecommerce.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false, unique = true)
    private String sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public void update(String name, BigDecimal price, Integer stock, String sku) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.sku = sku;
    }
}
