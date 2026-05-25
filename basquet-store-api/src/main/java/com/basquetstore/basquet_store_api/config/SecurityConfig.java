package com.basquetstore.basquet_store_api.config;

import com.basquetstore.basquet_store_api.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    //Configuración de seguridad para las rutas

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    //Asignamos que tipo de encriptación de contraseñas utilizaremos dentro de nuestro Spring Security
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configurar acceso a endpoints
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/api/debug/**").permitAll()
                        .requestMatchers("/api/shoes/**", "/api/clothing/**").permitAll()
                        // Endpoints de carrito y pedidos requieren autenticación
                        .requestMatchers("/api/cart/**", "/api/orders/**").authenticated()
                        // Admin solo para ciertos endpoints (lo manejaremos con anotaciones en controladores)
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(((request, response, authException) -> {
                            response.setContentType("application/json;charset_UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(String.format("""
                                    {
                                    "timestamp": "%s",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Se requiere autenticacion para continuar"
                                    }
                                    """, LocalDateTime.now()));
                        }))
                        .accessDeniedHandler(((request, response, accessDeniedException) -> {
                            response.setContentType("application/json;charset_UTF-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write(String.format("""
                                    {
                                    "timestamp": "%s",
                                    "status": 403,
                                    "error": "Forbidden",
                                    "message": "No tienes los permisos necesarios para continuar"
                                    }
                                    """, LocalDateTime.now()));
                        })))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}

