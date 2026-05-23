package com.basquetstore.basquet_store_api.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class UpdateShoeCartItemRequest {

    @PositiveOrZero(message = "La cantidad debe ser 0 o mayor")
    private int quantity;

}
