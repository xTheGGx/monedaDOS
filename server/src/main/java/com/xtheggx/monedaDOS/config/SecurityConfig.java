package com.xtheggx.monedaDOS.config;

import com.xtheggx.monedaDOS.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF: No es necesario en APIs Stateless porque no usamos cookies de sesión del navegador
                .csrf(AbstractHttpConfigurer::disable)
                
                // 2. Habilitar CORS: Permite que el cliente (Frontend) en otro dominio/puerto consuma la API
                .cors(cors -> cors.configure(http))
                
                // 3. Definir reglas de autorización de rutas
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso público a endpoints de autenticación (Login/Registro)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Permitir acceso a error para ver respuestas JSON de excepciones
                        .requestMatchers("/error").permitAll()
                        // Cualquier otra petición requiere un Token válido
                        .anyRequest().authenticated()
                )
                
                // 4. Gestión de Sesión STATELESS: Spring Security no creará ni usará HttpSession (JSESSIONID)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // 5. Integrar proveedor de autenticación y el filtro JWT antes del filtro estándar de Username/Password
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}