package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.AddClothingToCartRequest;
import com.basquetstore.basquet_store_api.dto.request.AddShoeToCartRequest;
import com.basquetstore.basquet_store_api.dto.request.UpdateClothingCartItemRequest;
import com.basquetstore.basquet_store_api.dto.request.UpdateShoeCartItemRequest;
import com.basquetstore.basquet_store_api.dto.response.ClothingCartItemResponse;
import com.basquetstore.basquet_store_api.dto.response.ShoeCartItemResponse;
import com.basquetstore.basquet_store_api.dto.response.CartResponse;
import com.basquetstore.basquet_store_api.entity.*;
import com.basquetstore.basquet_store_api.exception.BadRequestException;
import com.basquetstore.basquet_store_api.exception.InsufficientStockException;
import com.basquetstore.basquet_store_api.exception.ResourceNotFoundException;
import com.basquetstore.basquet_store_api.repository.CartRepository;
import com.basquetstore.basquet_store_api.repository.ClothingRepository;
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
    //Agregamos el clothingRepository para poder trabajar la indumentaria
    private final ClothingRepository clothingRepository;

    //Método auxiliar
    private CartResponse mapToResponse(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getShoeItems().stream()
                        .map(item -> new ShoeCartItemResponse(item.getShoeId(), item.getSize(), item.getQuantity()))
                        .collect(Collectors.toList()),
                cart.getClothingItems().stream()
                        .map(item -> new ClothingCartItemResponse(item.getClothingId(), item.getSize(), item.getQuantity()))
                        .collect(Collectors.toList())
        );
    }

    //Método auxiliar
    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setShoeItems(new ArrayList<>());
                    newCart.setClothingItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public CartResponse getCart(String userId) {
        return mapToResponse(getOrCreateCart(userId));
    }

    @Override
    public CartResponse addShoeItem(String userId, AddShoeToCartRequest request) {

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
        Optional<ShoeCartItem> existingItem = cart.getShoeItems().stream()
                .filter(item -> item.getShoeId().equals(request.getShoeId()) && item.getSize() == request.getSize())
                .findFirst();

        if(existingItem.isPresent()){ //Si existe en el carrito, sumamos cantidad
            int newQuantity = existingItem.get().getQuantity() + request.getQuantity();

            existingItem.get().setQuantity(newQuantity);
        }
        else{ //Si el producto no existe en el carrito, lo agregamos
            ShoeCartItem newItem = new ShoeCartItem();
            newItem.setShoeId(request.getShoeId());
            newItem.setSize(request.getSize());
            newItem.setQuantity(request.getQuantity());
            cart.getShoeItems().add(newItem);
        }

        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse updateShoeItemQuantity(String userId, String shoeId, int size, UpdateShoeCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        //Buscamos el item a actualizar
        ShoeCartItem item = cart.getShoeItems().stream()
                .filter(i -> i.getShoeId().equals(shoeId) && i.getSize() == size) //Comprobamos coincidencia de id de zapatilla y talle con el item
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Item no encontrado en el carrito."));

        //Si lo encuentra, comprobamos que el request tenga cantidad > 0. Si no, lo elimina
        if(request.getQuantity() == 0){
            cart.getShoeItems().remove(item);
        }
        else{
            item.setQuantity(request.getQuantity());
        }

        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse removeShoeItem(String userId, String shoeId, int size) {

        Cart cart = getOrCreateCart(userId);

        cart.getShoeItems().removeIf(item -> item.getShoeId().equals(shoeId) && item.getSize() == size);
        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }

    //Implementamos los nuevos métodos (van a ser lo mismo que los anteriores, pero ahora trabajando Indumentaria

    @Override
    public CartResponse addClothingItem(String userId, AddClothingToCartRequest request) {
        Cart cart = getOrCreateCart(userId);

        Clothing clothing = clothingRepository.findById(request.getClothingId())
                .orElseThrow(() -> new ResourceNotFoundException("La indumentaria no fue encontrada."));

        ClothingVariant variant = clothing.getVariants().stream() //De la zapatilla, extraemos sus SizeVariant, para comprobar si hay stock y talle coincidentes
                .filter(v -> v.getSize().equalsIgnoreCase(request.getSize()))
                .findFirst()
                .orElseThrow(() -> new InsufficientStockException("Stock de talle no encontrado."));

        //Validamos que el producto a agregar NO SE ENCUENTRE ACTUALMENTE en el carrito
        Optional<ClothingCartItem> existingItem = cart.getClothingItems().stream()
                .filter(item -> item.getClothingId().equals(request.getClothingId()) && item.getSize().equalsIgnoreCase(request.getSize()))
                .findFirst();

        if(existingItem.isPresent()){ //Si existe en el carrito, sumamos cantidad
            int newQuantity = existingItem.get().getQuantity() + request.getQuantity();

            existingItem.get().setQuantity(newQuantity);
        }
        else{ //Si el producto no existe en el carrito, lo agregamos
            ClothingCartItem newItem = new ClothingCartItem();
            newItem.setClothingId(request.getClothingId());
            newItem.setSize(request.getSize());
            newItem.setQuantity(request.getQuantity());
            cart.getClothingItems().add(newItem);
        }

        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse updateClothingItemQuantity(String userId, String clothingId, String size, UpdateClothingCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        ClothingCartItem item = cart.getClothingItems().stream()
                .filter(i -> i.getClothingId().equals(clothingId) && i.getSize().equalsIgnoreCase(size))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Item no encontrado en el carrito."));

        //Si lo encuentra, comprobamos que el request tenga cantidad > 0. Si no, lo elimina
        if(request.getQuantity() == 0){
            cart.getClothingItems().remove(item);
        }
        else{
            item.setQuantity(request.getQuantity());
        }

        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse removeClothingItem(String userId, String clothingId, String size) {
        Cart cart = getOrCreateCart(userId);

        cart.getClothingItems().removeIf(item -> item.getClothingId().equals(clothingId) && item.getSize().equalsIgnoreCase(size));
        cartRepository.save(cart);
        return mapToResponse(cart);
    }
}
