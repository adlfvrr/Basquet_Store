package com.basquetstore.basquet_store_api.repository;

import com.basquetstore.basquet_store_api.entity.Order;
import com.basquetstore.basquet_store_api.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {

    // USER ve sus pedidos, con filtro por status opcional
    Page<Order> findByUserIdAndStatus(String userId, OrderStatus status, Pageable pageable);
    Page<Order> findByUserId(String userId, Pageable pageable);

    // ADMIN ve todos, con filtro por status opcional
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
