package com.basquetstore.basquet_store_api.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "carts")
@Getter @Setter
public class Cart {

    @Id
    private String id;
    @Field("userId")
    private String userId;
    @Field("shoeItems")
    private List<ShoeCartItem> shoeItems = new ArrayList<>();
    @Field("clothingItems")
    private List<ClothingCartItem> clothingItems = new ArrayList<>();

    public Cart(){}

    public Cart(String userId, List<ShoeCartItem> items, List<ClothingCartItem> clothingItems){
        this.userId = userId;
        this.shoeItems = items;
        this.clothingItems = clothingItems;
    }

}
