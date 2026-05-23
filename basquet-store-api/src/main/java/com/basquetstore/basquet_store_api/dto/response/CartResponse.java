package com.basquetstore.basquet_store_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CartResponse {

    private String id;
    private String userId;
    private List<ShoeCartItemResponse> shoeItems;
    private List<ClothingCartItemResponse> clothingItems;

}
