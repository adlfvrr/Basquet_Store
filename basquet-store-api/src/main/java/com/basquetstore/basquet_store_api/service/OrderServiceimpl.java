package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.UpdateOrderStatusRequest;
import com.basquetstore.basquet_store_api.dto.response.OrderClothingItemResponse;
import com.basquetstore.basquet_store_api.dto.response.OrderDetailsResponse;
import com.basquetstore.basquet_store_api.dto.response.OrderShoeItemResponse;
import com.basquetstore.basquet_store_api.dto.response.OrderResponse;
import com.basquetstore.basquet_store_api.entity.*;
import com.basquetstore.basquet_store_api.exception.BadRequestException;
import com.basquetstore.basquet_store_api.exception.InsufficientStockException;
import com.basquetstore.basquet_store_api.exception.ResourceNotFoundException;
import com.basquetstore.basquet_store_api.exception.StatusUpdateException;
import com.basquetstore.basquet_store_api.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
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
    //Agregamos repositorio de indumentaria para trabajar con la misma
    private final ClothingRepository clothingRepository;

    //Por el momento, el precio de envío y tiempo aproximado (7 dias) estarán puestos por default
    private final Long shippingTime = 604800000L;
    private final BigDecimal shippingPrice = BigDecimal.valueOf(65);

    //Método auxiliar
    private void returnStock(Order order) {
        for (ShoeOrderItem item : order.getShoeItems()) {
            Shoe shoe = shoeRepository.findById(item.getShoeId()).orElse(null);
            if (shoe != null) {
                shoe.getVariants().stream()
                        .filter(v -> v.getSize() == item.getSize())
                        .findFirst()
                        .ifPresent(v -> v.setStock(v.getStock() + item.getQuantity()));
                shoeRepository.save(shoe);
            }
        }
        for (ClothingOrderItem item : order.getClothingItems()) {
            Clothing clothing = clothingRepository.findById(item.getClothingId()).orElse(null);
            if (clothing != null) {
                clothing.getVariants().stream()
                        .filter(v -> v.getSize().equalsIgnoreCase(item.getSize()))
                        .findFirst()
                        .ifPresent(v -> v.setStock(v.getStock() + item.getQuantity()));
                clothingRepository.save(clothing);
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
                order.getShoeItems().stream()
                        .map(item -> new OrderShoeItemResponse(item.getShoeId(),
                                item.getSize(),
                                item.getQuantity(),
                                item.getUnitPrice()))
                        .collect(Collectors.toList()),
                //Ahora lo mismo con los items de indumentaria
                order.getClothingItems().stream()
                        .map(item -> new OrderClothingItemResponse(item.getClothingId(),
                                item.getSize(),
                                item.getQuantity(),
                                item.getUnitPrice()))
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
        if (cart.getShoeItems().isEmpty() && cart.getClothingItems().isEmpty()) {
            throw new BadRequestException("El carrito se encuentra vacío.");
        }

        //Validamos stock
        List<ShoeOrderItem> shoeOrderItems = new ArrayList<>();
        for (ShoeCartItem shoeCartItem : cart.getShoeItems()) {
            Shoe shoe = shoeRepository.findById(shoeCartItem.getShoeId()) //Buscamos el objeto zapatilla dentro de la bdd
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));

            ShoeVariant variant = shoe.getVariants().stream()//Extraemos los SizeVariant de la misma
                    .filter(v -> v.getSize() == shoeCartItem.getSize()) //Buscamos el talle solicitado
                    .findFirst()
                    .orElseThrow(() -> new InsufficientStockException("Talle sin stock."));

            if (variant.getStock() < shoeCartItem.getQuantity()) { //Comprobamos que la cantidad deseada no supere el stock real
                throw new InsufficientStockException("Stock insuficiente para " + shoe.getModel() + " talle: " + shoeCartItem.getSize());
            }

            //Descontamos el stock
            variant.setStock(variant.getStock() - shoeCartItem.getQuantity());

            //Creamos el OrderItem
            ShoeOrderItem shoeOrderItem = new ShoeOrderItem();
            shoeOrderItem.setShoeId(shoeCartItem.getShoeId());
            shoeOrderItem.setSize(shoeCartItem.getSize());
            shoeOrderItem.setQuantity(shoeCartItem.getQuantity());
            shoeOrderItem.setUnitPrice(shoe.getPrice());
            shoeOrderItems.add(shoeOrderItem);

            shoeRepository.save(shoe);
        }

        //Ahora lo mismo, pero con los items de indumentaria
        List<ClothingOrderItem> clothingOrderItems = new ArrayList<>();
        for (ClothingCartItem clothingCartItem : cart.getClothingItems()) {
            Clothing clothing = clothingRepository.findById(clothingCartItem.getClothingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));

            ClothingVariant variant = clothing.getVariants().stream()//Extraemos los SizeVariant de la misma
                    .filter(v -> v.getSize().equalsIgnoreCase(clothingCartItem.getSize()))
                    .findFirst()
                    .orElseThrow(() -> new InsufficientStockException("Talle sin stock."));

            if (variant.getStock() < clothingCartItem.getQuantity()) {
                throw new InsufficientStockException("Stock insuficiente para " + clothing.getModel() + " talle: " + clothingCartItem.getSize());
            }

            variant.setStock(variant.getStock() - clothingCartItem.getQuantity());

            ClothingOrderItem clothingOrderItem = new ClothingOrderItem();
            clothingOrderItem.setClothingId(clothingCartItem.getClothingId());
            clothingOrderItem.setSize(clothingCartItem.getSize());
            clothingOrderItem.setQuantity(clothingCartItem.getQuantity());
            clothingOrderItem.setUnitPrice(clothing.getPrice());
            clothingOrderItems.add(clothingOrderItem);

            clothingRepository.save(clothing);
        }

        //Creamos pedido
        Order order = new Order();
        order.setUserId(userId);
        order.setDate(Instant.now());
        order.setStatus(OrderStatus.PENDIENTE); //Automáticamente asignamos el pedido como PENDIENTE
        order.setShoeItems(shoeOrderItems);
        order.setClothingItems(clothingOrderItems);

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
        for (ShoeOrderItem item : order.getShoeItems()) {
            price = price.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        //Ahora con los de indumentaria
        for(ClothingOrderItem item : order.getClothingItems()){
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
