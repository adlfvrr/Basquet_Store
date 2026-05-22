package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.AddToCartRequest;
import com.basquetstore.basquet_store_api.dto.request.UpdateCartItemRequest;
import com.basquetstore.basquet_store_api.dto.response.CartItemResponse;
import com.basquetstore.basquet_store_api.dto.response.CartResponse;
import com.basquetstore.basquet_store_api.entity.Cart;
import com.basquetstore.basquet_store_api.entity.CartItem;
import com.basquetstore.basquet_store_api.entity.Shoe;
import com.basquetstore.basquet_store_api.entity.ShoeVariant;
import com.basquetstore.basquet_store_api.exception.BadRequestException;
import com.basquetstore.basquet_store_api.exception.InsufficientStockException;
import com.basquetstore.basquet_store_api.exception.ResourceNotFoundException;
import com.basquetstore.basquet_store_api.repository.CartRepository;
import com.basquetstore.basquet_store_api.repository.ShoeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CartServiceImpl implements CartService{

    //Servicio de carrito

    private final CartRepository cartRepository;
    private final ShoeRepository shoeRepository;

    //Método auxiliar
    private CartResponse mapToResponse(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getItems().stream()
                        .map(item -> new CartItemResponse(item.getShoeId(), item.getSize(), item.getQuantity()))
                        .collect(Collectors.toList())
        );
    }

    //Método auxiliar
    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public CartResponse getCart(String userId) {
        return mapToResponse(getOrCreateCart(userId));
    }

    @Override
    public CartResponse addItem(String userId, AddToCartRequest request) {

        Cart cart = getOrCreateCart(userId);

        //Validamos existencia de productos
        Shoe shoe = shoeRepository.findById(request.getShoeId())
                .orElseThrow(() -> new ResourceNotFoundException("La zapatilla no fue encontrada."));
        //Validamos existencia de talle
        ShoeVariant variant = shoe.getVariants().stream() //De la zapatilla, extraemos sus SizeVariant, para comprobar si hay stock y talle coincidentes
                .filter(v -> v.getSize() == request.getSize())
                .findFirst()
                .orElseThrow(() -> new InsufficientStockException("Stock de talle no encontrado."));

        //Validamos que el producto a agregar NO SE ENCUENTRE ACTUALMENTE en el carrito
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getShoeId().equals(request.getShoeId()) && item.getSize() == request.getSize())
                .findFirst();

        if(existingItem.isPresent()){ //Si existe en el carrito, sumamos cantidad
            int newQuantity = existingItem.get().getQuantity() + request.getQuantity();

            existingItem.get().setQuantity(newQuantity);
        }
        else{ //Si el producto no existe en el carrito, lo agregamos
            CartItem newItem = new CartItem();
            newItem.setShoeId(request.getShoeId());
            newItem.setSize(request.getSize());
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse updateItemQuantity(String userId, String shoeId, int size, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        //Buscamos el item a actualizar
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getShoeId().equals(shoeId) && i.getSize() == size) //Comprobamos coincidencia de id de zapatilla y talle con el item
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Item no encontrado en el carrito."));

        //Si lo encuentra, comprobamos que el request tenga cantidad > 0. Si no, lo elimina
        if(request.getQuantity() == 0){
            cart.getItems().remove(item);
        }
        else{
            item.setQuantity(request.getQuantity());
        }

        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse removeItem(String userId, String shoeId, int size) {

        Cart cart = getOrCreateCart(userId);

        cart.getItems().removeIf(item -> item.getShoeId().equals(shoeId) && item.getSize() == size);
        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }
}
