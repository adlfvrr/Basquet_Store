package com.basquetstore.basquet_store_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class ClothingCartItem {

    //Creamos una clase para trabajar la indumentaria dentro del carrito

    private String clothingId;
    private String size;
    private int quantity;

}
