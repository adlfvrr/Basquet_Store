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

    // Agregamos un método que mongodb autoimplementa para saber si una shoe existe a partir de la marca y modelo, asi evitamos duplicados
    boolean existsByBrandAndModel(String brand, String model);

    // Filtro para el tipo de zapatilla (general o KIDS)
    Page<Shoe> findByShoeType(String shoeType, Pageable pageable);

    //Filtro combinado por los 3
    @Query("{ 'brand' : ?0, 'variants.size' : ?1, 'shoeType' : ?2}")
    Page<Shoe> findByBrandAndSizeAndShoeType(String brand, int size, String shoeType, Pageable pageable);
    //Filtros combinados con cada uno
    @Query("{ 'brand' : ?0, 'shoeType' : ?1 }")
    Page<Shoe> findByBrandAndShoeType(String brand, String shoeType, Pageable pageable);

    @Query("{ 'variants.size' : ?0, 'shoeType' : ?1 }")
    Page<Shoe> findBySizeAndShoeType(int size, String shoeType, Pageable pageable);

}
