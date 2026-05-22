package com.basquetstore.basquet_store_api.repository;

import com.basquetstore.basquet_store_api.entity.Clothing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ClothingRepository extends MongoRepository<Clothing, String> {

    // Filtro por marca
    Page<Clothing> findByBrand(String brand, Pageable pageable);

    // Filtro por talle: busca shoes que tengan una variante con ese size
    @Query("{ 'variants.size' : ?0 }")
    Page<Clothing> findBySize(String size, Pageable pageable);

    // Filtro combinado: marca y talle
    @Query("{ 'brand' : ?0, 'variants.size' : ?1 }")
    Page<Clothing> findByBrandAndSize(String brand, String size, Pageable pageable);

    // Agregamos un método que mongodb autoimplementa para saber si una shoe existe a partir de la marca y modelo, asi evitamos duplicados
    boolean existsByBrandAndModel(String brand, String model);

    // Filtro para la sección
    Page<Clothing> findBySection(String section, Pageable pageable);

    //Filtro combinado por los 3
    @Query("{ 'brand' : ?0, 'variants.size' : ?1, 'section' : ?2}")
    Page<Clothing> findByBrandAndSizeAndSection(String brand, String size, String section, Pageable pageable);
    //Filtros combinados con cada uno
    @Query("{ 'brand' : ?0, 'section' : ?1 }")
    Page<Clothing> findByBrandAndSection(String brand, String section, Pageable pageable);

    @Query("{ 'variants.size' : ?0, 'section' : ?1 }")
    Page<Clothing> findBySizeAndSection(String size, String section, Pageable pageable);


}
