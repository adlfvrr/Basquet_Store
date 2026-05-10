package com.basquetstore.basquet_store_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "shoes")
@AllArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
public class Shoe {

    @Id
    private String id;
    @Field("brand")
    private String brand;
    @Field("model")
    private String model;
    @Field("description")
    private String description;
    @Field("price")
    private BigDecimal price;
    @Field("imageURL")
    private String imageUrl;
    @Field("variants")
    private List<SizeVariant> variants = new ArrayList<>();

}
