package com.xtheggx.monedaDOS.auth;

import com.xtheggx.monedaDOS.jwt.JwtService;
import com.xtheggx.monedaDOS.model.Rol;
import com.xtheggx.monedaDOS.model.UserDetailsImpl;
import com.xtheggx.monedaDOS.model.Usuario;
import com.xtheggx.monedaDOS.repository.RolRepository;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest loginRequest) {
        // Validación de entrada
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es requerido");
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }

        // Normalizar el email (trimear y convertir a minúsculas)
        String normalizedEmail = loginRequest.getUsername().trim().toLowerCase();

        Usuario user = usuarioRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, loginRequest.getPassword()));
        } catch (Exception e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        HashMap<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("uid", user.getIdUsuario());

        String token = jwtService.getToken(extraClaims, userDetails);
        return AuthResponse.builder()
                .token(token)
                .build();

    }

    public AuthResponse register(RegisterRequest registerRequest) {
        if (usuarioRepository.findByEmailIgnoreCase(registerRequest.getEmail()).isPresent()) {
            throw  new RuntimeException("El email ya está en uso.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registerRequest.getNombre());
        nuevoUsuario.setApellidoPaterno(registerRequest.getApellidoPat());
        nuevoUsuario.setApellidoMaterno(registerRequest.getApellidoMat());
        nuevoUsuario.setEmail(registerRequest.getEmail());

        // 2. Encriptar la contraseña
        nuevoUsuario.setContrasena(passwordEncoder.encode(registerRequest.getPassword()));

        // 3. Asignar el ROL por defecto
        Rol rolUsuario = rolRepository.findByNombreIgnoreCase("USER")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'USER' no encontrado."));

        nuevoUsuario.setRol(rolUsuario);

        usuarioRepository.save(nuevoUsuario);
        UserDetailsImpl user = UserDetailsImpl.build(nuevoUsuario);

        HashMap<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("uid", nuevoUsuario.getIdUsuario()); // Guardamos el ID en el token

        return AuthResponse.builder()
                .token(jwtService.getToken(extraClaims, user))
                .build();
    }

}
