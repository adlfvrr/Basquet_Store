package com.basquetstore.basquet_store_api.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "clothing")
@Getter
@Setter
public class Clothing {

    //Creamos la indumentaria

    @Id
    private String id;
    @Field("brand")
    private String brand;
    @Field("model")
    private String model;
    @Field("description")
    private String description;
    //Mediante sección podemos discernir entre: Ropa interior, superior, inferior, etc
    @Field("section")
    private String section;
    @Field("imageUrl")
    private String imageUrl;
    @Field("price")
    private BigDecimal price;
    @Field("variants")
    private List<ClothingVariant> variants = new ArrayList<>();

    public Clothing() {

    }

    public Clothing(String brand, String model, String description, String section, String imageUrl, BigDecimal price, List<ClothingVariant> variants) {
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.section = section;
        this.imageUrl = imageUrl;
        this.price = price;
        this.variants = variants;
    }
}
