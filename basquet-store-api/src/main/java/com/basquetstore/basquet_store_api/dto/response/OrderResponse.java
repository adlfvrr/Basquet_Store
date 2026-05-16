package com.basquetstore.basquet_store_api.dto.response;

import com.basquetstore.basquet_store_api.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class OrderResponse {

    private String id;
    private String userId;
    private Instant date;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private OrderDetailsResponse details;

}
