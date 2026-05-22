package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.AddClothingRequest;
import com.basquetstore.basquet_store_api.dto.response.ClothingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClothingService {

    //Agregamos el tipo de indumentaria para el correcto funcionamiento del controlador
    Page<ClothingResponse> findAll(String brand, String size, String section, Pageable pageable);

    ClothingResponse findById(String id);

    ClothingResponse addClothing(AddClothingRequest clothingRequest);

}
