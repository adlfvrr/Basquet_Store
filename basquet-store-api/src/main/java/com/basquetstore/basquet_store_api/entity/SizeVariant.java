package com.basquetstore.basquet_store_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
public class SizeVariant {

    //SizeVariant permite guardar dentro de cada shoe su talle y stock de manera separada.
    private int size;
    private int stock;

}
