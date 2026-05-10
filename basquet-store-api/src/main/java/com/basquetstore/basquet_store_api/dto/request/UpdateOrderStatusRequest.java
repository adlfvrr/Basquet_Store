package com.basquetstore.basquet_store_api.dto.request;

import com.basquetstore.basquet_store_api.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class UpdateOrderStatusRequest {

    @NotNull(message = "El estado es obligatorio")
    private OrderStatus status;

}
