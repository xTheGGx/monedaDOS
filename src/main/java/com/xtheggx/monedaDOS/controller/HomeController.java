package com.xtheggx.monedaDOS.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xtheggx.monedaDOS.dto.RegistroDTO;
import com.xtheggx.monedaDOS.model.Rol;
import com.xtheggx.monedaDOS.model.Usuario;
import com.xtheggx.monedaDOS.repository.RolRepository;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import com.xtheggx.monedaDOS.service.UsuarioService;


@Controller
public class HomeController {

    @GetMapping({"/", "/index"})
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home() {
        return "home/index"; // templates/home/index.html (tu dashboard)
    }

}