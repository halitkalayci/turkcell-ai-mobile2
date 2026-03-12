package com.turkcell.ecommerce.order.service;

import com.turkcell.ecommerce.order.dto.*;
import com.turkcell.ecommerce.order.entity.Order;
import com.turkcell.ecommerce.order.entity.OrderItem;
import com.turkcell.ecommerce.order.exception.InsufficientStockException;
import com.turkcell.ecommerce.order.exception.OrderNotFoundException;
import com.turkcell.ecommerce.order.repository.OrderRepository;
import com.turkcell.ecommerce.product.entity.Product;
import com.turkcell.ecommerce.product.exception.ProductNotFoundException;
import com.turkcell.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = findProductOrThrow(itemRequest.getProductId());
            validateStock(product, itemRequest.getQuantity());
            product.decreaseStock(itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            order.addItem(orderItem);
        }

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Override
    public void deleteOrder(UUID id) {
        Order order = findByIdOrThrow(id);

        for (OrderItem item : order.getItems()) {
            Product product = findProductOrThrow(item.getProductId());
            product.increaseStock(item.getQuantity());
            productRepository.save(product);
        }

        orderRepository.delete(order);
    }

    private Order findByIdOrThrow(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private Product findProductOrThrow(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new InsufficientStockException(
                    product.getId(), requestedQuantity, product.getStock());
        }
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal totalPrice = order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .customerId(order.getCustomerId())
                .totalPrice(totalPrice)
                .items(itemResponses)
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }
}
