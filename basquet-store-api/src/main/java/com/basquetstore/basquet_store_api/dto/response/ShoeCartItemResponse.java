package com.basquetstore.basquet_store_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ShoeCartItemResponse {

    private String shoeId;
    private int size;
    private int quantity;

}
