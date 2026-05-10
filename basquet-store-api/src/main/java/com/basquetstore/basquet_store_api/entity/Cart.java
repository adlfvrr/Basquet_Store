package com.basquetstore.basquet_store_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "carts")
@AllArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
public class Cart {

    @Id
    private String id;
    @Field("userId")
    private String userId;
    @Field("items")
    private List<CartItem> items = new ArrayList<>();

}
