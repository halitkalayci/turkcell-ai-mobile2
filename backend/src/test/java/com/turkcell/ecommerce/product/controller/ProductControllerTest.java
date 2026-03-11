package com.turkcell.ecommerce.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkcell.ecommerce.product.dto.ProductRequest;
import com.turkcell.ecommerce.product.dto.ProductResponse;
import com.turkcell.ecommerce.product.exception.ProductNotFoundException;
import com.turkcell.ecommerce.product.exception.SkuAlreadyExistsException;
import com.turkcell.ecommerce.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    // --- GET /api/v1/products ---

    @Test
    void getAllProducts_returns200WithProductList() throws Exception {
        ProductResponse response = aProductResponse(UUID.randomUUID(), "Laptop", "SKU-001");
        given(productService.getAllProducts()).willReturn(List.of(response));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value("SKU-001"));
    }

    // --- GET /api/v1/products/{id} ---

    @Test
    void getProductById_returns200_whenProductExists() throws Exception {
        UUID id = UUID.randomUUID();
        ProductResponse response = aProductResponse(id, "Laptop", "SKU-001");
        given(productService.getProductById(id)).willReturn(response);

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void getProductById_returns404_whenProductNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        given(productService.getProductById(id)).willThrow(new ProductNotFoundException(id));

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound());
    }

    // --- POST /api/v1/products ---

    @Test
    void createProduct_returns201_whenRequestIsValid() throws Exception {
        ProductRequest request = aProductRequest("Laptop", "SKU-001");
        ProductResponse response = aProductResponse(UUID.randomUUID(), "Laptop", "SKU-001");
        given(productService.createProduct(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU-001"));
    }

    @Test
    void createProduct_returns400_whenRequestIsInvalid() throws Exception {
        ProductRequest invalidRequest = ProductRequest.builder()
                .name("")
                .price(new BigDecimal("-1"))
                .stock(-1)
                .sku("")
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_returns409_whenSkuAlreadyExists() throws Exception {
        ProductRequest request = aProductRequest("Laptop", "SKU-001");
        given(productService.createProduct(any())).willThrow(new SkuAlreadyExistsException("SKU-001"));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // --- PUT /api/v1/products/{id} ---

    @Test
    void updateProduct_returns200_whenRequestIsValid() throws Exception {
        UUID id = UUID.randomUUID();
        ProductRequest request = aProductRequest("Güncel Laptop", "SKU-001");
        ProductResponse response = aProductResponse(id, "Güncel Laptop", "SKU-001");
        given(productService.updateProduct(eq(id), any())).willReturn(response);

        mockMvc.perform(put("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Güncel Laptop"));
    }

    // --- DELETE /api/v1/products/{id} ---

    @Test
    void deleteProduct_returns204_whenProductExists() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_returns404_whenProductNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ProductNotFoundException(id)).when(productService).deleteProduct(id);

        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound());
    }

    // --- Test Builders ---

    private ProductResponse aProductResponse(UUID id, String name, String sku) {
        return ProductResponse.builder()
                .id(id)
                .name(name)
                .price(new BigDecimal("99.99"))
                .stock(10)
                .sku(sku)
                .build();
    }

    private ProductRequest aProductRequest(String name, String sku) {
        return ProductRequest.builder()
                .name(name)
                .price(new BigDecimal("99.99"))
                .stock(10)
                .sku(sku)
                .build();
    }
}
