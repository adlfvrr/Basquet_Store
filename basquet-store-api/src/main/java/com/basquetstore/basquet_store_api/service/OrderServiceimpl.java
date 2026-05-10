package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.UpdateOrderStatusRequest;
import com.basquetstore.basquet_store_api.dto.response.OrderItemResponse;
import com.basquetstore.basquet_store_api.dto.response.OrderResponse;
import com.basquetstore.basquet_store_api.entity.*;
import com.basquetstore.basquet_store_api.exception.BadRequestException;
import com.basquetstore.basquet_store_api.exception.InsufficientStockException;
import com.basquetstore.basquet_store_api.exception.ResourceNotFoundException;
import com.basquetstore.basquet_store_api.exception.StatusUpdateException;
import com.basquetstore.basquet_store_api.repository.CartRepository;
import com.basquetstore.basquet_store_api.repository.OrderRepository;
import com.basquetstore.basquet_store_api.repository.ShoeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceimpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ShoeRepository shoeRepository;

    private void returnStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Shoe shoe = shoeRepository.findById(item.getShoeId()).orElse(null);
            if (shoe != null) {
                shoe.getVariants().stream()
                        .filter(v -> v.getSize() == item.getSize())
                        .findFirst()
                        .ifPresent(v -> v.setStock(v.getStock() + item.getQuantity()));
                shoeRepository.save(shoe);
            }
        }
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getDate(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(item.getShoeId(), item.getSize(), item.getQuantity(), item.getUnitPrice()))
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    @Override
    public OrderResponse createOrder(String userId) {
        //Creamos el carrito y verificamos que no esté vacío
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()-> new BadRequestException("El carrito se encuentra vacío."));
        if(cart.getItems().isEmpty()){
            throw new BadRequestException("El carrito se encuentra vacío.");
        }

        //Validamos stock
        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cart.getItems()){
            Shoe shoe = shoeRepository.findById(cartItem.getShoeId())
                    .orElseThrow(()-> new ResourceNotFoundException("Producto no encontrado."));
            SizeVariant variant = shoe.getVariants().stream()
                    .filter(v -> v.getSize() == cartItem.getSize())
                    .findFirst()
                    .orElseThrow(() -> new InsufficientStockException("Talle sin stock."));
            if(variant.getStock() < cartItem.getQuantity()){
                throw new InsufficientStockException("Stock insuficiente para " + shoe.getModel() + " talle: " + cartItem.getSize());
            }

            //Descontamos el stock
            variant.setStock(variant.getStock() - cartItem.getQuantity());

            //Creamos el OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setShoeId(cartItem.getShoeId());
            orderItem.setSize(cartItem.getSize());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(shoe.getPrice());
            orderItems.add(orderItem);

            shoeRepository.save(shoe);
        }
        //Creamos orden
        Order order = new Order();
        order.setUserId(userId);
        order.setDate(Instant.now());
        order.setStatus(OrderStatus.PENDIENTE);
        order.setItems(orderItems);
        orderRepository.save(order);

        cartRepository.deleteByUserId(userId);

        return mapToResponse(order);
    }

    @Override
    public Page<OrderResponse> findOrders(String userId, OrderStatus status, Pageable pageable) {
        if(status != null && userId != null) return orderRepository.findByUserIdAndStatus(userId, status, pageable).map(this::mapToResponse);

        if(userId != null) return orderRepository.findByUserId(userId, pageable).map(this::mapToResponse);

        if(status != null) return orderRepository.findByStatus(status, pageable).map(this::mapToResponse);

        return orderRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public OrderResponse findById(String userId, String orderId) {
        //El userId lo utilizaremos más adelante, donde se controlará según el rol (admin o usuario)
        return mapToResponse(orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException("Pedido no encontrado.")));
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(String orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        OrderStatus newStatus = request.getStatus();

        //Validamos los pases de estados.
        if(order.getStatus() == OrderStatus.CANCELADO) throw new StatusUpdateException("No se puede cambiar un pedido cancelado.");
        if(order.getStatus() == OrderStatus.PENDIENTE && (newStatus != OrderStatus.CONFIRMADO && newStatus != OrderStatus.CANCELADO)) throw new StatusUpdateException("Transición no permitida.");
        if(order.getStatus() == OrderStatus.CONFIRMADO && newStatus == OrderStatus.CANCELADO){
            returnStock(order);
        }
        else{
            throw new StatusUpdateException("Transición no permitida.");
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
        return mapToResponse(order);

    }
}
