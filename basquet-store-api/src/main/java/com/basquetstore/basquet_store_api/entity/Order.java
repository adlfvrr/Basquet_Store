package com.basquetstore.basquet_store_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
    @Field("items")
    private List<OrderItem> items = new ArrayList<>();

    public Order(){}

    public Order(String userId, Instant date, OrderStatus status, List<OrderItem> items){
        this.userId = userId;
        this.date = date;
        this.status = status;
        this.items = items;
    }
}
