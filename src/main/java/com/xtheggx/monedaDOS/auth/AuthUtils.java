package com.xtheggx.monedaDOS.auth;

import com.xtheggx.monedaDOS.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtils {
    private final UsuarioService usuarioService; // agrega un método: Long getIdByEmail(String email)

    public Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // por defecto, username = email usado en login
        return usuarioService.getIdByEmail(email);
    }
}