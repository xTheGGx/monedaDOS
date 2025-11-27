package com.xtheggx.monedaDOS.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    private UserDetailsImpl(Long id,
                            String email,
                            String password,
                            Collection<? extends GrantedAuthority> authorities,
                            boolean enabled) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
    }

    public static UserDetailsImpl build(Usuario u) {
        Objects.requireNonNull(u, "Usuario nulo en UserDetailsImpl.build");

        String role = "USER";
        if (u.getRol() != null && u.getRol().getNombre() != null) {
            role = u.getRol().getNombre().trim().toUpperCase(Locale.ROOT);
        }

        List<GrantedAuthority> auths = List.of(new SimpleGrantedAuthority(role));

        return new UserDetailsImpl(
                u.getIdUsuario(),
                u.getEmail(),
                u.getContrasena(),
                auths,
                true
        );
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
