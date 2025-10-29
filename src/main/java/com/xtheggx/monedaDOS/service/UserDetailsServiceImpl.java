package com.xtheggx.monedados.service;

import com.xtheggx.monedados.model.UserDetailsImpl;
import com.xtheggx.monedados.model.Usuario;
import com.xtheggx.monedados.repository.UsuarioRepository;
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
        Usuario userInfo = Optional.ofNullable(usuarioRepository.findByEmailIgnoreCase(username))
                .orElseThrow();
        return UserDetailsImpl.build(userInfo);
    }

    

}
