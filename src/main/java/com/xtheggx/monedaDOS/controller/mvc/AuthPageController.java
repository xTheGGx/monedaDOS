package com.xtheggx.monedaDOS.controller.mvc;

import com.xtheggx.monedaDOS.auth.AuthResponse;
import com.xtheggx.monedaDOS.auth.AuthService;
import com.xtheggx.monedaDOS.auth.LoginRequest;
import com.xtheggx.monedaDOS.auth.RegisterRequest;
import com.xtheggx.monedaDOS.dto.RegistroDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;

@Controller
@RequiredArgsConstructor
public class AuthPageController {
    private final AuthService authService;

    @GetMapping("/login")
    public String loginView() {
        return "auth/login"; // templates/auth/login.html
    }

    @GetMapping("/register")
    public String registerView(Model model) {
        model.addAttribute("usuarioForm", new RegistroDTO());
        return "auth/signup_form";
    }

    // Puente: el form HTML (x-www-form-urlencoded) entra aquí,
    // llamamos a tu AuthService y dejamos el JWT en cookie
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String loginForm(
            @RequestParam String email,
            @RequestParam String contrasena,
            HttpServletResponse response,
            RedirectAttributes ra
    ) {
        try {
            AuthResponse jwt = authService.login(new LoginRequest(email, contrasena));

            // cookie con el token para que JwtAuthenticationFilter la lea
            ResponseCookie cookie = ResponseCookie.from("MONEDADOS_TOKEN", jwt.getToken())
                    .httpOnly(true)
                    .secure(false)      // true si usa HTTPS
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(Duration.ofHours(8))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return "redirect:/panel";
        } catch (Exception e) {
            ra.addAttribute("error", "true");
            return "redirect:/login?error=true";
        }
    }

    // (opcional) Si manejas registro por formulario MVC:
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String registerForm(
            @RequestParam String nombre,
            @RequestParam String apellidoPaterno,
            @RequestParam String apellidoMaterno,
            @RequestParam String email,
            @RequestParam String contrasena,
            RedirectAttributes ra
    ) {
        try {
            authService.register(new RegisterRequest(nombre, apellidoPaterno, apellidoMaterno, email, contrasena));
            return "redirect:/login?registered=1";
        } catch (Exception e) {
            ra.addAttribute("error", "true");
            return "redirect:/register?error=true";
        }
    }
}