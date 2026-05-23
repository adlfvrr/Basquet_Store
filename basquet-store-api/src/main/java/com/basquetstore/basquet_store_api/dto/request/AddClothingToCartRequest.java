package com.basquetstore.basquet_store_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class AddClothingToCartRequest {

    //Creamos el request para agregar indumentaria al carrito

    @NotBlank(message = "El ID del producto es obligatorio.")
    private String clothingId;

    @NotBlank(message = "El talle es obligatorio.")
    private String size;

    @Positive(message = "La cantidad debe ser mayor a 0.")
    private int quantity;


}
