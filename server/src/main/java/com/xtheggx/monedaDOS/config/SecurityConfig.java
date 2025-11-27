package com.xtheggx.monedaDOS.config;

import com.xtheggx.monedaDOS.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                
                // 2. Habilitar CORS: Permite que el cliente en otro dominio/puerto consuma la API
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))                
                
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
                
                // 5. Filtros
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir orígenes específicos del frontend
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Cabeceras permitidas 
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        
        // Permitir enviar credenciales (cookies, headers de autorización)
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}