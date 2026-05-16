package com.basquetstore.basquet_store_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
public class OrderDetailsResponse {

    private Instant deliverDate;
    private String address;
    private BigDecimal shippingPrice;
    private BigDecimal totalPrice;

}
