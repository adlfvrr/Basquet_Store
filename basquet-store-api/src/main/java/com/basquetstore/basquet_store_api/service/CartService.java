package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.AddClothingToCartRequest;
import com.basquetstore.basquet_store_api.dto.request.AddShoeToCartRequest;
import com.basquetstore.basquet_store_api.dto.request.UpdateClothingCartItemRequest;
import com.basquetstore.basquet_store_api.dto.request.UpdateShoeCartItemRequest;
import com.basquetstore.basquet_store_api.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(String userId);

    CartResponse addShoeItem(String userId, AddShoeToCartRequest request);

    CartResponse updateShoeItemQuantity(String userId, String shoeId, int size, UpdateShoeCartItemRequest request);

    CartResponse removeShoeItem(String userId, String shoeId, int size);

    void clearCart(String userId);

    //Agregamos funcionalidades para que el carrito pueda trabajar ahora con indumentaria
    CartResponse addClothingItem(String userId, AddClothingToCartRequest request);

    CartResponse updateClothingItemQuantity(String userId, String clothingId, String size, UpdateClothingCartItemRequest request);

    CartResponse removeClothingItem(String userId, String clothingId, String size);

}
