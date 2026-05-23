package com.basquetstore.basquet_store_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
public class ShoeCartItem {

    //Cambiamos nombre para distinguir entre zapatillas e indumentarias

    private String shoeId;
    private int size;
    private int quantity;

}
