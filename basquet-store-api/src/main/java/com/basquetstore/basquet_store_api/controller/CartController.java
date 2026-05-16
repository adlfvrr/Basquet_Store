package com.basquetstore.basquet_store_api.controller;

import com.basquetstore.basquet_store_api.dto.request.AddToCartRequest;
import com.basquetstore.basquet_store_api.dto.request.UpdateCartItemRequest;
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

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(Principal principal, @Valid @RequestBody AddToCartRequest request) {
        //Podríamos hacer un método del controlador para evitarnos este paso, pero como es nuevo, por ahora hagamoslo así.
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(cartService.addItem(user.getId(), request));
    }

    //Permite, dentro del carrito, actualizar la cantidad que deseamos
    @PutMapping("/items/{shoeId}/{size}")
    public ResponseEntity<CartResponse> updateitemQuantity(Principal principal,
                                                           @PathVariable String shoeId,
                                                           @PathVariable int size,
                                                           @Valid @RequestBody UpdateCartItemRequest request) {

        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(cartService.updateItemQuantity(user.getId(), shoeId, size, request));
    }

    @DeleteMapping("/items/{shoeId}/{size}")
    public ResponseEntity<CartResponse> removeItem(Principal principal,
                                                   @PathVariable String shoeId,
                                                   @PathVariable int size) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(cartService.removeItem(user.getId(), shoeId, size));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        cartService.clearCart(user.getId());
        return ResponseEntity.noContent().build();
    }

}
