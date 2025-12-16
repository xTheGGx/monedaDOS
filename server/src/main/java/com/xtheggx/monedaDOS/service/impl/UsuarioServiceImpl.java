package com.xtheggx.monedaDOS.service.impl;

import java.util.List;

import com.xtheggx.monedaDOS.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.xtheggx.monedaDOS.model.Rol;
import com.xtheggx.monedaDOS.model.Usuario;
import com.xtheggx.monedaDOS.repository.RolRepository;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import com.xtheggx.monedaDOS.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    // --- DEPENDENCIAS ---
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // --- CONSTRUCTOR ACTUALIZADO ---
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registrarUsuario(RegisterRequest registroDTO) {

        // 1. Convertir DTO a Entidad
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registroDTO.getNombre());
        nuevoUsuario.setApellidoPaterno(registroDTO.getApellidoPat());
        nuevoUsuario.setApellidoMaterno(registroDTO.getApellidoMat());
        nuevoUsuario.setEmail(registroDTO.getEmail());

        // 2. Encriptar la contraseña
        nuevoUsuario.setContrasena(passwordEncoder.encode(registroDTO.getPassword()));

        // 3. Asignar el ROL por defecto
        Rol rolUsuario = rolRepository.findByNombreIgnoreCase("USER")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'USER' no encontrado."));

        nuevoUsuario.setRol(rolUsuario);

        // 4. Guardar el nuevo usuario en la BD
        usuarioRepository.save(nuevoUsuario);
    }


    // --- MÉTODOS CRUD  ---

    @Override
    public Long getIdByEmail(String email) {
        return usuarioRepository.findIdByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }
    @Override
    public Usuario createUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario updateUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }
}