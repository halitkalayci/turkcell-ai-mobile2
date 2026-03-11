package com.turkcell.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Ürün adı boş olamaz")
    @Size(min = 2, max = 255, message = "Ürün adı 2-255 karakter arasında olmalıdır")
    private String name;

    @NotNull(message = "Fiyat boş olamaz")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fiyat 0'dan büyük olmalıdır")
    private BigDecimal price;

    @NotNull(message = "Stok boş olamaz")
    @Min(value = 0, message = "Stok 0 veya daha fazla olmalıdır")
    private Integer stock;

    @NotBlank(message = "SKU boş olamaz")
    private String sku;
}
