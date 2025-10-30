package com.xtheggx.monedaDOS.controller;

import com.xtheggx.monedaDOS.dto.RegistroDTO;
import com.xtheggx.monedaDOS.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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

    // POST /register -> registra y redirige a login
    @PostMapping("/register")
    public String processRegister(@ModelAttribute("usuarioForm") RegistroDTO dto) {
        usuarioService.registrarUsuario(dto);
        return "redirect:/login?registered=1";
    }
}