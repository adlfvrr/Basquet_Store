package com.basquetstore.basquet_store_api.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
@Getter @Setter
public class Order {

    @Id
    private String id;
    @Field("userId")
    private String userId;
    @Field("date")
    private Instant date;
    @Field("status")
    private OrderStatus status;
    @Field("shoeItems")
    private List<ShoeOrderItem> shoeItems = new ArrayList<>();
    @Field("clothingItems")
    private List<ClothingOrderItem> clothingItems = new ArrayList<>();
    @Field("details")
    private OrderDetails orderDetails; //Guardamos detalles del envío como dirección, precio, llegada aproximada del pedido, etc

    public Order(){}

    public Order(String userId, Instant date, OrderStatus status, List<ShoeOrderItem> shoeItems, List<ClothingOrderItem> clothingItems, OrderDetails details){
        this.userId = userId;
        this.date = date;
        this.status = status;
        this.shoeItems = shoeItems;
        this.clothingItems = clothingItems;
        this.orderDetails = details;
    }
}
