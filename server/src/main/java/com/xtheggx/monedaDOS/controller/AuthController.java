package com.xtheggx.monedaDOS.controller;

import com.xtheggx.monedaDOS.auth.AuthResponse;
import com.xtheggx.monedaDOS.auth.AuthService;
import com.xtheggx.monedaDOS.auth.LoginRequest;
import com.xtheggx.monedaDOS.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginJwt(@RequestBody LoginRequest loginRequest) {
        AuthResponse res = authService.login(loginRequest);
        return buildCookieResponse(res);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerJwt(@RequestBody RegisterRequest registerRequest) {
        AuthResponse res = authService.register(registerRequest);
        return buildCookieResponse(res);
    }

    // Endpoint para Logout (Borrar cookie)
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false) // false para localhost, true para prod
                .path("/")
                .maxAge(0) // Expira inmediatamente
                .sameSite("Strict")
                .build();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    private ResponseEntity<AuthResponse> buildCookieResponse(AuthResponse res) {
        // Crear la cookie segura
        ResponseCookie cookie = ResponseCookie.from("accessToken", res.getToken())
                .httpOnly(true)       // JS no puede leerla
                .secure(false)        // Cambiar a true en producción (HTTPS)
                .path("/")            // Disponible para toda la app
                .maxAge(24 * 60 * 60) // 1 día 
                .sameSite("Strict")   // Protección CSRF
                .build();

        // Enviamos el token también en el body por si el front lo necesita para algo,
        // pero la clave es el header SET_COOKIE.
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }
}