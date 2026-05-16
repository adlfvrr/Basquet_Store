package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.UpdateOrderStatusRequest;
import com.basquetstore.basquet_store_api.dto.response.OrderDetailsResponse;
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
import com.basquetstore.basquet_store_api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceimpl implements OrderService {

    //Servicio de pedidos

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ShoeRepository shoeRepository;
    //Agregamos repositorio de usuario para acceder a sus datos para el armado de pedidos
    private final UserRepository userRepository;

    //Por el momento, el precio de envío y tiempo aproximado (7 dias) estarán puestos por default
    private final Long shippingTime = 604800000L;
    private final BigDecimal shippingPrice = BigDecimal.valueOf(65);

    //Método auxiliar
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

    //Método auxiliar
    private OrderResponse mapToResponse(Order order) {

        User user = userRepository.findById(order.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        //Armamos el response con los datos del pedido y usuario
        OrderDetailsResponse detailsResponse = new OrderDetailsResponse(order.getOrderDetails().getDeliverDate(),
                order.getOrderDetails().getAddress(),
                order.getOrderDetails().getShippingPrice(),
                order.getOrderDetails().getTotalPrice());

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getDate(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(item.getShoeId(), item.getSize(), item.getQuantity(), item.getUnitPrice()))
                        .collect(Collectors.toList()),
                //Asignamos el orderDetails
                detailsResponse
        );
    }

    @Transactional
    //Transactional indica que, en caso de que se produzca una excepción, se reviertan TODAS las acciones (Evitando errores de actualizaciones)
    @Override
    public OrderResponse createOrder(String userId) {
        //Creamos el carrito y verificamos que no esté vacío
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("El carrito se encuentra vacío."));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("El carrito se encuentra vacío.");
        }

        //Validamos stock
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Shoe shoe = shoeRepository.findById(cartItem.getShoeId()) //Buscamos el objeto zapatilla dentro de la bdd
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));

            SizeVariant variant = shoe.getVariants().stream()//Extraemos los SizeVariant de la misma
                    .filter(v -> v.getSize() == cartItem.getSize()) //Buscamos el talle solicitado
                    .findFirst()
                    .orElseThrow(() -> new InsufficientStockException("Talle sin stock."));

            if (variant.getStock() < cartItem.getQuantity()) { //Comprobamos que la cantidad deseada no supere el stock real
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
        
        //Creamos pedido
        Order order = new Order();
        order.setUserId(userId);
        order.setDate(Instant.now());
        order.setStatus(OrderStatus.PENDIENTE); //Automáticamente asignamos el pedido como PENDIENTE
        order.setItems(orderItems);

        //Creamos detalles del pedido
        OrderDetails orderDetails = new OrderDetails();
        //Obtenemos usuario para asignar la dirección
        User user = userRepository.findById(order.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        orderDetails.setAddress(user.getAddress());
        //Asignamos tiempo aproximado de envío
        orderDetails.setDeliverDate(Instant.now().plusMillis(this.shippingTime));
        orderDetails.setShippingPrice(this.shippingPrice);
        BigDecimal price = BigDecimal.ZERO;
        //Creamos precio total
        for (OrderItem item : order.getItems()) {
            price = price.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        //Asignamos sumándole el precio del coste de envío
        orderDetails.setTotalPrice(price.add(this.shippingPrice));

        //Asignamos los details al pedido
        order.setOrderDetails(orderDetails);

        //Persistimos
        orderRepository.save(order);

        cartRepository.deleteByUserId(userId); //Eliminamos el carrito del usuario, pues ya hizo el pedido

        return mapToResponse(order);
    }

    @Override
    public Page<OrderResponse> findOrders(String userId, OrderStatus status, Pageable pageable) {
        //Buscamos los pedidos según los datos que recibamos del controlador (status e id de usuario)
        if (status != null && userId != null)
            return orderRepository.findByUserIdAndStatus(userId, status, pageable).map(this::mapToResponse);

        if (userId != null) return orderRepository.findByUserId(userId, pageable).map(this::mapToResponse);

        if (status != null) return orderRepository.findByStatus(status, pageable).map(this::mapToResponse);

        return orderRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public OrderResponse findById(String userId, String orderId) {
        //El userId lo utilizaremos más adelante, donde se controlará según el rol (admin o usuario)
        return mapToResponse(orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado.")));
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(String orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        OrderStatus newStatus = request.getStatus();

        //Validamos los pases de estados.

        //No se reconocerá otro estado, y un pedido (ya sea confirmado o cancelado) no puede volver a ser pendiente
        if (newStatus != OrderStatus.CANCELADO && newStatus != OrderStatus.CONFIRMADO)
            throw new StatusUpdateException("Transición no permitida: No se reconoce el estado, o bien no puede volver a ser 'PENDIENTE'");

        //No se puede cambiar el estado de un pedido cancelado.
        if (order.getStatus() == OrderStatus.CANCELADO)
            throw new StatusUpdateException("Transición no permitida: La orden ya se encuentra cancelada");

        //Si se cancela un pedido, ya sea confirmado o pendiente, se retorna el stock
        if (newStatus == OrderStatus.CANCELADO) {
            returnStock(order);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
        return mapToResponse(order);

    }
}
