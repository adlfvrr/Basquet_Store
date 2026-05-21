package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.AddShoeRequest;
import com.basquetstore.basquet_store_api.dto.response.ShoeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShoeService {

    //Agregamos el tipo de calzado para el correcto funcionamiento del controlador
    Page<ShoeResponse> findAll(String brand, Integer size, String shoeType, Pageable pageable);

    ShoeResponse findById(String id);

    //Añadimos nuevo método: Añadir zapatilla
    ShoeResponse addShoe(AddShoeRequest shoeRequest);

}
