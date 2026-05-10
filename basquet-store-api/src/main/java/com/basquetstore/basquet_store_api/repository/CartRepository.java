package com.basquetstore.basquet_store_api.repository;

import com.basquetstore.basquet_store_api.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {

    Optional<Cart> findByUserId(String userId);
    void deleteByUserId(String userId);

}