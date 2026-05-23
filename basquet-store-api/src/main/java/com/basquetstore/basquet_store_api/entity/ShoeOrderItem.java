package com.basquetstore.basquet_store_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ShoeOrderItem {

    private String shoeId;
    private int size;
    private int quantity;
    private BigDecimal unitPrice;

}
