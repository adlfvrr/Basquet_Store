package com.basquetstore.basquet_store_api.repository;

import com.basquetstore.basquet_store_api.entity.Shoe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ShoeRepository extends MongoRepository<Shoe, String> {

    // Filtro por marca
    Page<Shoe> findByBrand(String brand, Pageable pageable);

    // Filtro por talle: busca shoes que tengan una variante con ese size
    @Query("{ 'variants.size' : ?0 }")
    Page<Shoe> findBySize(int size, Pageable pageable);

    // Filtro combinado: marca y talle
    @Query("{ 'brand' : ?0, 'variants.size' : ?1 }")
    Page<Shoe> findByBrandAndSize(String brand, int size, Pageable pageable);
}
