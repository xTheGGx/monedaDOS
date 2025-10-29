// src/main/java/mx/dgtic/sfp/service/AuthService.java
package com.xtheggx.monedaDOS.service;

import com.xtheggx.monedaDOS.dto.RegistroDTO;
import com.xtheggx.monedaDOS.model.Rol;
import com.xtheggx.monedaDOS.model.Usuario;
import com.xtheggx.monedaDOS.repository.RolRepository;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService{

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;

    public boolean emailExiste(String email) {
        return email != null && usuarioRepository.existsByEmailIgnoreCase(email.trim());
    }

    @Transactional
    public Usuario registrarNuevoUsuario(RegistroDTO dto) {
        if (emailExiste(dto.getEmail())) {
            throw new IllegalStateException("El correo ya está registrado");
        }
        if (!dto.getContrasena().equals(dto.getConfirmarContrasena())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        Rol rolUser = rolRepository.findByNombreIgnoreCase("USER")
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setNombre("USER");
                    r.setDescripcion("Rol de usuario estándar");
                    return rolRepository.save(r);
                });

        String hash = BCrypt.hashpw(dto.getContrasena(), BCrypt.gensalt(12));

        Usuario u = new Usuario();
        u.setNombre(dto.getNombre());
        u.setApellidoPaterno(dto.getApellidoPaterno());
        u.setApellidoMaterno(dto.getApellidoMaterno());
        u.setEmail(dto.getEmail().trim().toLowerCase());
        u.setContrasena(hash);
        u.setFechaRegistro(LocalDateTime.now());
        u.setRol(rolUser);

        return usuarioRepository.save(u);
    }

    public Optional<Usuario> autenticar(String email, String contrasenaPlano) {
        if (email == null || contrasenaPlano == null) return Optional.empty();

        return usuarioRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .filter(u -> BCrypt.checkpw(contrasenaPlano, u.getContrasena()));
    }
}
