package com.turkcell.ecommerce.product.service;

import com.turkcell.ecommerce.product.dto.ProductRequest;
import com.turkcell.ecommerce.product.dto.ProductResponse;
import com.turkcell.ecommerce.product.entity.Product;
import com.turkcell.ecommerce.product.exception.ProductNotFoundException;
import com.turkcell.ecommerce.product.exception.SkuAlreadyExistsException;
import com.turkcell.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        assertSkuIsUnique(request.getSku());
        return toResponse(productRepository.save(toEntity(request)));
    }

    @Override
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = findByIdOrThrow(id);
        assertSkuIsUniqueForUpdate(request.getSku(), id);
        product.update(request.getName(), request.getPrice(), request.getStock(), request.getSku());
        return toResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(UUID id) {
        productRepository.delete(findByIdOrThrow(id));
    }

    private Product findByIdOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private void assertSkuIsUnique(String sku) {
        if (productRepository.existsBySku(sku)) {
            throw new SkuAlreadyExistsException(sku);
        }
    }

    private void assertSkuIsUniqueForUpdate(String sku, UUID id) {
        if (productRepository.existsBySkuAndIdNot(sku, id)) {
            throw new SkuAlreadyExistsException(sku);
        }
    }

    private Product toEntity(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .sku(request.getSku())
                .build();
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .sku(product.getSku())
                .build();
    }
}
