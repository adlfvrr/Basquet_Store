package com.basquetstore.basquet_store_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class ClothingResponse {

    private String id;
    private String brand;
    private String model;
    private String description;
    private String section;
    private BigDecimal price;
    private String imageUrl;
    private List<ClothingVariantResponse> variants;

}
