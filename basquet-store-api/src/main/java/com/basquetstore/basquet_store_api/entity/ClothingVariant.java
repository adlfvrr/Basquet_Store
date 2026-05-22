package com.basquetstore.basquet_store_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class ClothingVariant {

    //Es necesario crear otro Variant, ya que ahora el talle se distingue con letras (XS, S, M, etc)

    private String size;
    private int stock;

}
