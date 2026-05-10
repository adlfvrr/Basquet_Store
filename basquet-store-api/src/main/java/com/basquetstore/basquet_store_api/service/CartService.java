package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.AddToCartRequest;
import com.basquetstore.basquet_store_api.dto.request.UpdateCartItemRequest;
import com.basquetstore.basquet_store_api.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(String userId);

    CartResponse addItem(String userId, AddToCartRequest request);

    CartResponse updateItemQuantity(String userId, String shoeId, int size, UpdateCartItemRequest request);

    CartResponse removeItem(String userId, String shoeId, int size);

    void clearCart(String userId);

}
