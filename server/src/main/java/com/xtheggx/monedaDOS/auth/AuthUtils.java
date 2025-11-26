package com.xtheggx.monedaDOS.auth;

import com.xtheggx.monedaDOS.model.UserDetailsImpl;
import com.xtheggx.monedaDOS.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    public Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
            //  Obtenemos el ID directamente de la sesión en memoria sin consultar la base de datos nuevamente.
            return userDetails.getId();
        }

        throw new IllegalStateException("No hay usuario autenticado en el contexto de seguridad");
    }
}