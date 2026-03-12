package com.turkcell.ecommerce.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {

    @NotNull(message = "Ürün ID boş olamaz")
    private UUID productId;

    @NotNull(message = "Adet boş olamaz")
    @Min(value = 1, message = "Adet en az 1 olmalıdır")
    private Integer quantity;
}
