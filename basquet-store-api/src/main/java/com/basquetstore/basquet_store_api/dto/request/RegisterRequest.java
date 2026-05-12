package com.basquetstore.basquet_store_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jdk.jfr.Registered;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
public class RegisterRequest {

    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "Debe ser un email válido.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 4, max = 20, message = "El nombre de usuario tiene un mínimo de 4 caracteres y máximo de 20.")
    private String username;

    @NotBlank(message = "El nombre es obligatorio.")
    private String name;

    @NotBlank(message = "El número de teléfono es obligatorio.")
    @Pattern(regexp = "^\\d{10}$", message = "Formato de teléfono inválido.")
    private String phone;

    @NotBlank(message = "La dirección es obligatoria.")
    private String address;

}
