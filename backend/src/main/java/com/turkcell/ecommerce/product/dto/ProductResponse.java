package com.turkcell.ecommerce.product.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private UUID id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String sku;
}
