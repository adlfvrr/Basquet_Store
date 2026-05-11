package com.basquetstore.basquet_store_api.controller;

import com.basquetstore.basquet_store_api.dto.request.UpdateOrderStatusRequest;
import com.basquetstore.basquet_store_api.dto.response.OrderResponse;
import com.basquetstore.basquet_store_api.entity.OrderStatus;
import com.basquetstore.basquet_store_api.entity.User;
import com.basquetstore.basquet_store_api.exception.ResourceNotFoundException;
import com.basquetstore.basquet_store_api.repository.UserRepository;
import com.basquetstore.basquet_store_api.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    //Ahora si vamos a hacer el método de UserId

    private String getUserIdPrincipal(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return user.getId();
    }

    private String getUserIdOptional(Principal principal) {
        if (principal == null) return null;
        return getUserIdPrincipal(principal);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(Principal principal) {
        return ResponseEntity.ok(orderService.createOrder(getUserIdPrincipal(principal)));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> listOrders(Principal principal,
                                                          @RequestParam(required = false) OrderStatus status,
                                                          @PageableDefault(size = 4) Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(orderService.findOrders(null, status, pageable));
        } else {
            return ResponseEntity.ok(orderService.findOrders(getUserIdPrincipal(principal), status, pageable));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id, Principal principal){
        OrderResponse order = orderService.findById(getUserIdOptional(principal), id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) &&
        !order.getUserId().equals(getUserIdPrincipal(principal))){
            throw new ResourceNotFoundException("Pedido no encontrado");
        }
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable String id,
                                                      @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }

}
