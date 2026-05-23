package com.basquetstore.basquet_store_api.controller;

import com.basquetstore.basquet_store_api.dto.request.AddClothingToCartRequest;
import com.basquetstore.basquet_store_api.dto.request.AddShoeToCartRequest;
import com.basquetstore.basquet_store_api.dto.request.UpdateClothingCartItemRequest;
import com.basquetstore.basquet_store_api.dto.request.UpdateShoeCartItemRequest;
import com.basquetstore.basquet_store_api.dto.response.CartResponse;
import com.basquetstore.basquet_store_api.entity.User;
import com.basquetstore.basquet_store_api.exception.ResourceNotFoundException;
import com.basquetstore.basquet_store_api.repository.UserRepository;
import com.basquetstore.basquet_store_api.service.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/cart")
@AllArgsConstructor
public class CartController {

    //Controlador de carrito

    private final CartService cartService;
    private final UserRepository userRepository;

    /*
    Anotación: Principal permite pasar al controlador la identidad autenticada del usuario. Es interpretado de manera automática
    por Spring Security. Es decir, sabe como reconocer al usuario que hizo la petición HTTP.
     */

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(cartService.getCart(user.getId()));

    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        cartService.clearCart(user.getId());
        return ResponseEntity.noContent().build();
    }

    //CARRITO ZAPATILLAS
    @PostMapping("/shoes/items")
    public ResponseEntity<CartResponse> addShoeItem(Principal principal, @Valid @RequestBody AddShoeToCartRequest request) {
        //Podríamos hacer un método del controlador para evitarnos este paso, pero como es nuevo, por ahora hagamoslo así.
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(cartService.addShoeItem(user.getId(), request));
    }

    //Permite, dentro del carrito, actualizar la cantidad que deseamos
    @PutMapping("/shoes/items/{shoeId}/{size}")
    public ResponseEntity<CartResponse> updateShoeItemQuantity(Principal principal,
                                                               @PathVariable String shoeId,
                                                               @PathVariable int size,
                                                               @Valid @RequestBody UpdateShoeCartItemRequest request) {

        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(cartService.updateShoeItemQuantity(user.getId(), shoeId, size, request));
    }

    @DeleteMapping("/shoes/items/{shoeId}/{size}")
    public ResponseEntity<CartResponse> removeShoeItem(Principal principal,
                                                       @PathVariable String shoeId,
                                                       @PathVariable int size) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(cartService.removeShoeItem(user.getId(), shoeId, size));
    }


    //Ahora, para trabajar con los items de indumentaria, cambiamos las rutas e implementamos nuevas funcionalidades:

    @PostMapping("/clothing/items")
    public ResponseEntity<CartResponse> addClothingItem(Principal principal, @Valid @RequestBody AddClothingToCartRequest request) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(cartService.addClothingItem(user.getId(), request));
    }

    @PutMapping("/clothing/items/{shoeId}/{size}")
    public ResponseEntity<CartResponse> updateClothingItemQuantity(Principal principal,
                                                                   @PathVariable String shoeId,
                                                                   @PathVariable String size,
                                                                   @Valid @RequestBody UpdateClothingCartItemRequest request) {

        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(cartService.updateClothingItemQuantity(user.getId(), shoeId, size, request));
    }

    @DeleteMapping("/clothing/items/{shoeId}/{size}")
    public ResponseEntity<CartResponse> removeClothingItem(Principal principal,
                                                           @PathVariable String shoeId,
                                                           @PathVariable String size) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(cartService.removeClothingItem(user.getId(), shoeId, size));
    }
}


