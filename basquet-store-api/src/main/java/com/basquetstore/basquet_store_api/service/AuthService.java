package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.LoginRequest;
import com.basquetstore.basquet_store_api.dto.request.RegisterRequest;
import com.basquetstore.basquet_store_api.dto.response.JwtResponse;

public interface AuthService {

    JwtResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);

}
