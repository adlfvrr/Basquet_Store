package com.basquetstore.basquet_store_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClothingOrderItem {

    //Creamos, para trabajar los items de indumentaria, la clase que irá dentro de Order

    private String clothingId;
    private String size;
    private int quantity;
    private BigDecimal unitPrice;

}
