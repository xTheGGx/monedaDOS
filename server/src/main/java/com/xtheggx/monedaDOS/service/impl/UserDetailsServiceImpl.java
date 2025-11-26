package com.xtheggx.monedaDOS.service.impl;

import com.xtheggx.monedaDOS.model.UserDetailsImpl;
import com.xtheggx.monedaDOS.model.Usuario;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

   @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ahora usamos el Optional directamente del repositorio
        Usuario userInfo = usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con ese email: " + username));
        return UserDetailsImpl.build(userInfo);
    }
}
