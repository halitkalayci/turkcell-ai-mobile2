package com.turkcell.ecommerce.product.service;

import com.turkcell.ecommerce.product.dto.ProductRequest;
import com.turkcell.ecommerce.product.dto.ProductResponse;
import com.turkcell.ecommerce.product.entity.Product;
import com.turkcell.ecommerce.product.exception.ProductNotFoundException;
import com.turkcell.ecommerce.product.exception.SkuAlreadyExistsException;
import com.turkcell.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    // --- getAllProducts ---

    @Test
    void getAllProducts_returnsMappedResponses_whenProductsExist() {
        Product product = aProduct(UUID.randomUUID(), "Laptop", "SKU-001");
        given(productRepository.findAll()).willReturn(List.of(product));

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSku()).isEqualTo("SKU-001");
    }

    // --- getProductById ---

    @Test
    void getProductById_returnsResponse_whenProductExists() {
        UUID id = UUID.randomUUID();
        Product product = aProduct(id, "Laptop", "SKU-001");
        given(productRepository.findById(id)).willReturn(Optional.of(product));

        ProductResponse result = productService.getProductById(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Laptop");
    }

    @Test
    void getProductById_throwsProductNotFoundException_whenProductNotFound() {
        UUID id = UUID.randomUUID();
        given(productRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(id))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // --- createProduct ---

    @Test
    void createProduct_returnsResponse_whenSkuIsUnique() {
        ProductRequest request = aProductRequest("Laptop", "SKU-001");
        Product saved = aProduct(UUID.randomUUID(), "Laptop", "SKU-001");
        given(productRepository.existsBySku("SKU-001")).willReturn(false);
        given(productRepository.save(any())).willReturn(saved);

        ProductResponse result = productService.createProduct(request);

        assertThat(result.getSku()).isEqualTo("SKU-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_throwsSkuAlreadyExistsException_whenSkuIsDuplicate() {
        ProductRequest request = aProductRequest("Laptop", "SKU-001");
        given(productRepository.existsBySku("SKU-001")).willReturn(true);

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(SkuAlreadyExistsException.class);
    }

    // --- updateProduct ---

    @Test
    void updateProduct_returnsUpdatedResponse_whenValidRequest() {
        UUID id = UUID.randomUUID();
        Product product = aProduct(id, "Eski Ürün", "OLD-SKU");
        ProductRequest request = aProductRequest("Yeni Ürün", "NEW-SKU");
        given(productRepository.findById(id)).willReturn(Optional.of(product));
        given(productRepository.existsBySkuAndIdNot("NEW-SKU", id)).willReturn(false);
        given(productRepository.save(product)).willReturn(product);

        ProductResponse result = productService.updateProduct(id, request);

        assertThat(result.getName()).isEqualTo("Yeni Ürün");
        assertThat(result.getSku()).isEqualTo("NEW-SKU");
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_throwsSkuAlreadyExistsException_whenSkuBelongsToAnotherProduct() {
        UUID id = UUID.randomUUID();
        Product product = aProduct(id, "Laptop", "SKU-001");
        ProductRequest request = aProductRequest("Laptop", "SKU-002");
        given(productRepository.findById(id)).willReturn(Optional.of(product));
        given(productRepository.existsBySkuAndIdNot("SKU-002", id)).willReturn(true);

        assertThatThrownBy(() -> productService.updateProduct(id, request))
                .isInstanceOf(SkuAlreadyExistsException.class);
    }

    // --- deleteProduct ---

    @Test
    void deleteProduct_deletesProduct_whenProductExists() {
        UUID id = UUID.randomUUID();
        Product product = aProduct(id, "Laptop", "SKU-001");
        given(productRepository.findById(id)).willReturn(Optional.of(product));

        productService.deleteProduct(id);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_throwsProductNotFoundException_whenProductNotFound() {
        UUID id = UUID.randomUUID();
        given(productRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(id))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // --- Test Builders ---

    private Product aProduct(UUID id, String name, String sku) {
        return Product.builder()
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
