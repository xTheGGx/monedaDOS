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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        Usuario user = usuarioRepository.findByEmailIgnoreCase(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        HashMap<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("uid", user.getIdUsuario()); // Guardamos el ID en el token

        String token = jwtService.getToken(extraClaims, userDetails);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse register(RegisterRequest registerRequest) {
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
