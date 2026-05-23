package com.basquetstore.basquet_store_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class OrderShoeItemResponse {

    private String shoeId;
    private int size;
    private int quantity;
    private BigDecimal unitPrice;

}
