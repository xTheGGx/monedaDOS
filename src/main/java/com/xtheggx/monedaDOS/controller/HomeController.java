package com.xtheggx.monedados.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xtheggx.monedados.dto.RegistroDTO;
import com.xtheggx.monedados.dto.UsuarioDTO;
import com.xtheggx.monedados.exception.ResourceNotFoundException;
import com.xtheggx.monedados.model.Rol;
import com.xtheggx.monedados.model.Usuario;
import com.xtheggx.monedados.repository.RolRepository;
import com.xtheggx.monedados.repository.UsuarioRepository;
import com.xtheggx.monedados.service.UsuarioService;

import jakarta.mail.Quota.Resource;

@RestController
@RequestMapping("/api/users")
public class HomeController {
    
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public HomeController(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/login")
    public String showLoginForm() {
        // Devuelve el nombre de la plantilla 'login.html'
        return "login";
    }

    /**
     * Muestra el formulario de registro.
     * Usa el DTO 'RegistroUsuarioDTO' que creamos.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Pasa un objeto DTO vacío al formulario
        model.addAttribute("usuarioForm", new RegistroDTO());
        return "signup_form"; // Tu vista de registro (ej. signup_form.html)
    }

    /**
     * Procesa el formulario de registro.
     * Esto es lo que estabas buscando.
     */
    @PostMapping("/process_register")
    public String processRegister(RegistroDTO registroDTO) {

        // 1. Convertir el DTO a la Entidad Usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registroDTO.getNombre());
        nuevoUsuario.setApellidoPaterno(registroDTO.getApellidoPaterno());
        nuevoUsuario.setApellidoMaterno(registroDTO.getApellidoMaterno());
        nuevoUsuario.setEmail(registroDTO.getEmail());
        
        // 2. Encriptar la contraseña
        nuevoUsuario.setContrasena(passwordEncoder.encode(registroDTO.getContrasena()));

        // 3. Asignar el ROL por defecto (la lógica clave)
        // Buscamos el rol "USUARIO" en la BD.
        // (Asegúrate de que ese rol exista en tus "SEEDS" de la BD)
        Rol rolUsuario = rolRepository.findByNombreIgnoreCase("USER")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'USER' no encontrado."));
        
        // Esta es la diferencia: solo asignas UN objeto Rol, no un Set.
        nuevoUsuario.setRol(rolUsuario);

        // 4. Guardar el nuevo usuario en la BD
        usuarioRepository.save(nuevoUsuario);

        // 5. Redirigir a una página de éxito
        return "register_success"; 
    }

}
