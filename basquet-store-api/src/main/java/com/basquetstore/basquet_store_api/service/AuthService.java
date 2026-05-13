package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.LoginRequest;
import com.basquetstore.basquet_store_api.dto.request.RegisterRequest;
import com.basquetstore.basquet_store_api.dto.response.JwtResponse;

//Interfaz para el servicio de Autenticación.
public interface AuthService {

    JwtResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);

}
