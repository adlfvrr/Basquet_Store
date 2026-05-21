package com.basquetstore.basquet_store_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ShoeResponse {

    private String id;
    private String brand;
    private String model;
    private String description;
    private String shoeType; //Agregamos tipo de calzado al response
    private BigDecimal price;
    private String imageUrl;
    private List<SizeVariantResponse> variants;

}
