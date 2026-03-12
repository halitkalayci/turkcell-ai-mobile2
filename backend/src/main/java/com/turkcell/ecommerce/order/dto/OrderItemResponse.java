package com.turkcell.ecommerce.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private UUID id;
    private UUID productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
