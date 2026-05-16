package com.basquetstore.basquet_store_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class OrderDetails {

    //Esta clase permite guardar los detalles de envío

    private Instant deliverDate;

    private String address;

    private BigDecimal shippingPrice;

    private BigDecimal totalPrice;

}
