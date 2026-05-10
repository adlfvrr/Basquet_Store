package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.response.ShoeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShoeService {

    Page<ShoeResponse> findAll(String brand, Integer size, Pageable pageable);
    ShoeResponse findById(String id);

}
