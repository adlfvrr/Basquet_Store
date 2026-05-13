package com.basquetstore.basquet_store_api.controller;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    //Sirve para conocer a que bdd estamos conectados

    private final MongoTemplate mongoTemplate;

    public TestController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/api/debug/db")
    public String getDbInfo() {
        return "Database: " + mongoTemplate.getDb().getName() +
                " | Shoes count: " + mongoTemplate.getCollection("shoes").countDocuments();
    }
}