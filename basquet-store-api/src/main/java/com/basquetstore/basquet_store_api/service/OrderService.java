package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.UpdateOrderStatusRequest;
import com.basquetstore.basquet_store_api.dto.response.OrderResponse;
import com.basquetstore.basquet_store_api.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(String userId);
    Page<OrderResponse> findOrders(String userId, OrderStatus status, Pageable pageable);
    OrderResponse findById(String userId, String orderId);
    OrderResponse updateStatus(String orderId, UpdateOrderStatusRequest request);
}
