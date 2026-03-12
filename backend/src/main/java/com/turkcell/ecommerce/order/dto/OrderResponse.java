package com.turkcell.ecommerce.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private UUID id;
    private LocalDateTime orderDate;
    private String customerId;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
}
