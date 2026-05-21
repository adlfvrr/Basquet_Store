package com.basquetstore.basquet_store_api.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "shoes")
@Getter
@Setter
public class Shoe {

    @Id
    private String id;
    @Field("brand")
    private String brand;
    @Field("model")
    private String model;
    @Field("description")
    private String description;
    @Field("shoeType")
    private String shoeType;
    @Field("price")
    private BigDecimal price;
    @Field("imageURL")
    private String imageUrl;
    @Field("variants")
    private List<SizeVariant> variants = new ArrayList<>();

    public Shoe() {
    }

    public Shoe(String brand, String model, String description, String shoeType, BigDecimal price, String imageUrl, List<SizeVariant> variants) {
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.variants = variants;
        this.shoeType = shoeType;
    }
}
