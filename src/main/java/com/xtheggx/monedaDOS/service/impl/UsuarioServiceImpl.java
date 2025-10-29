package com.xtheggx.monedados.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;

import com.xtheggx.monedados.dto.RegistroDTO; 
import com.xtheggx.monedados.model.Rol; 
import com.xtheggx.monedados.model.Usuario;
import com.xtheggx.monedados.repository.RolRepository; 
import com.xtheggx.monedados.repository.UsuarioRepository;
import com.xtheggx.monedados.service.UsuarioService;

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

    // --- NUEVO MÉTODO DE NEGOCIO IMPLEMENTADO ---
    @Override
    public void registrarUsuario(RegistroDTO registroDTO) {
        
        // 1. Convertir DTO a Entidad
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registroDTO.getNombre());
        nuevoUsuario.setApellidoPaterno(registroDTO.getApellidoPaterno());
        nuevoUsuario.setApellidoMaterno(registroDTO.getApellidoMaterno());
        nuevoUsuario.setEmail(registroDTO.getEmail());
        
        // 2. Encriptar la contraseña
        nuevoUsuario.setContrasena(passwordEncoder.encode(registroDTO.getContrasena()));

        // 3. Asignar el ROL por defecto
        Rol rolUsuario = rolRepository.findByNombreIgnoreCase("USER")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'USER' no encontrado."));
        
        nuevoUsuario.setRol(rolUsuario);

        // 4. Guardar el nuevo usuario en la BD
        usuarioRepository.save(nuevoUsuario);
    }


    // --- MÉTODOS CRUD EXISTENTES ---

    @Override
    public Usuario createUsuario(Usuario usuario) {
        // ADVERTENCIA: Este método, tal como está, NO encripta la contraseña.
        // Es mejor usar 'registrarUsuario' para crear usuarios desde la web.
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