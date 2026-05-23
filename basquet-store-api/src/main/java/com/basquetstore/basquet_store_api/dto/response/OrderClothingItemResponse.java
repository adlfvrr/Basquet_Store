package com.basquetstore.basquet_store_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class OrderClothingItemResponse {

    private String clothingId;
    private String size;
    private int quantity;
    private BigDecimal unitPrice;

}
