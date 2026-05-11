package com.basquetstore.basquet_store_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter@Setter
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("username")
    private String username;

    @Indexed(unique = true)
    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Field("name")
    private String name;

    @Field("address")
    private String address;

    @Field("phone")
    private String phone;

    @Field("role")
    private Role role;

    @Field("enabled")
    private boolean enabled;

    public User(){}

    public User(String username, String email, String password, String name, String address, String phone, Role role, boolean enabled){
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.role = role;
        this.enabled = enabled;
    }
}
