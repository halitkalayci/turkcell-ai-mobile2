package com.turkcell.ecommerce.product.controller;

import com.turkcell.ecommerce.common.dto.ErrorResponse;
import com.turkcell.ecommerce.product.dto.ProductRequest;
import com.turkcell.ecommerce.product.dto.ProductResponse;
import com.turkcell.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Ürün yönetimi işlemleri")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Tüm ürünleri listele", operationId = "getAllProducts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ürün listesi başarıyla döndürüldü",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Sunucu içi hata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    @Operation(summary = "Yeni ürün oluştur", operationId = "createProduct")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ürün başarıyla oluşturuldu",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "SKU çakışması",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Sunucu içi hata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID ile ürün getir", operationId = "getProductById")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ürün başarıyla döndürüldü",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Kaynak bulunamadı",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Sunucu içi hata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Ürünü güncelle", operationId = "updateProduct")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ürün başarıyla güncellendi",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Kaynak bulunamadı",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "SKU çakışması",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Sunucu içi hata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id,
                                                         @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Ürünü sil", operationId = "deleteProduct")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ürün başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Kaynak bulunamadı",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Sunucu içi hata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
