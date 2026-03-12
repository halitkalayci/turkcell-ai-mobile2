package com.turkcell.ecommerce.order.controller;

import com.turkcell.ecommerce.common.dto.ErrorResponse;
import com.turkcell.ecommerce.order.dto.OrderRequest;
import com.turkcell.ecommerce.order.dto.OrderResponse;
import com.turkcell.ecommerce.order.service.OrderService;
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
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Sipariş yönetimi işlemleri")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Tüm siparişleri listele", operationId = "getAllOrders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sipariş listesi başarıyla döndürüldü",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Sunucu içi hata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PostMapping
    @Operation(summary = "Yeni sipariş oluştur", operationId = "createOrder")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sipariş başarıyla oluşturuldu",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ürün bulunamadı",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Yetersiz stok",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Sunucu içi hata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID ile sipariş getir", operationId = "getOrderById")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sipariş başarıyla döndürüldü",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Sipariş bulunamadı",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Sunucu içi hata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Siparişi sil", operationId = "deleteOrder")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sipariş başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Sipariş bulunamadı",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Sunucu içi hata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
