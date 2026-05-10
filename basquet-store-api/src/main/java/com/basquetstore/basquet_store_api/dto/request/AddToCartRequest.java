package com.basquetstore.basquet_store_api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class AddToCartRequest {

    @NotBlank(message = "El ID del producto es obligatorio.")
    private String shoeId;

    @Min(value = 39, message = "Talle mínimo 39.")
    @Max(value = 42, message = "Talle máximo 42.")
    private int size;

    @Positive(message = "La cantidad debe ser mayor a 0.")
    private int quantity;


}
