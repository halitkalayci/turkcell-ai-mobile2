package com.turkcell.ecommerce.order.service;

import com.turkcell.ecommerce.order.dto.OrderRequest;
import com.turkcell.ecommerce.order.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(UUID id);

    OrderResponse createOrder(OrderRequest request);

    void deleteOrder(UUID id);
}
