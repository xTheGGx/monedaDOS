package com.xtheggx.monedaDOS.controller;

import com.xtheggx.monedaDOS.auth.AuthResponse;
import com.xtheggx.monedaDOS.auth.AuthService;
import com.xtheggx.monedaDOS.auth.LoginRequest;
import com.xtheggx.monedaDOS.auth.RegisterRequest;
import com.xtheggx.monedaDOS.dto.RegistroDTO;
import com.xtheggx.monedaDOS.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthService authService;


    // GET /login -> renderiza la vista de login
    @GetMapping("/login")
    public String login() {
        return "auth/login"; // templates/auth/login.html
    }


    // GET /register -> formulario de alta
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("usuarioForm", new RegistroDTO());
        return "auth/signup_form"; // templates/auth/signup_form.html
    }

/*    // POST /register -> registra y redirige a login
    @PostMapping("/register")
    public String processRegister(@ModelAttribute("usuarioForm") RegistroDTO dto) {
        usuarioService.registrarUsuario(dto);
        return "redirect:/login?registered=1";
    }*/


    // RESPONSE JWT
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginJwt(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerJwt(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }
}