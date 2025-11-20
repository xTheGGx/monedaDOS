package com.xtheggx.monedaDOS.controller;

import com.xtheggx.monedaDOS.auth.AuthResponse;
import com.xtheggx.monedaDOS.auth.AuthService;
import com.xtheggx.monedaDOS.auth.LoginRequest;
import com.xtheggx.monedaDOS.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginJwt(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerJwt(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }
}