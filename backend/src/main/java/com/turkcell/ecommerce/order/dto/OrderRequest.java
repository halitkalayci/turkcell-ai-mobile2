package com.turkcell.ecommerce.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotBlank(message = "Müşteri kimliği boş olamaz")
    @Size(max = 255, message = "Müşteri kimliği en fazla 255 karakter olmalıdır")
    private String customerId;

    @NotEmpty(message = "Sipariş kalemleri boş olamaz")
    @Valid
    private List<OrderItemRequest> items;
}
