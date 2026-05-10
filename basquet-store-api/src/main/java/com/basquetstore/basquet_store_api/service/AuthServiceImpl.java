package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.LoginRequest;
import com.basquetstore.basquet_store_api.dto.request.RegisterRequest;
import com.basquetstore.basquet_store_api.dto.response.JwtResponse;
import com.basquetstore.basquet_store_api.entity.Role;
import com.basquetstore.basquet_store_api.entity.User;
import com.basquetstore.basquet_store_api.repository.UserRepository;
import com.basquetstore.basquet_store_api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadCredentialsException("El mail ya se encuentra registrado.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        user.setRole(Role.USUARIO);
        user.setEnabled(true);

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return new JwtResponse(token, user.getEmail(), user.getRole().name());
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BadCredentialsException("Credenciales inválidas."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return new JwtResponse(token, user.getEmail(), user.getRole().name());
    }
}
