package com.xtheggx.monedaDOS.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
* UserDetailsImpl mapea Usuario base de datos a springsecurity a nivel de entidad
 */
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String nombre;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;
    private Usuario usuario;

    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
    }

    public UserDetailsImpl(Long id, String nombre , String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.authorities = authorities;
    }


    public static UserDetailsImpl build(Usuario user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRol().getNombre()));
        return new UserDetailsImpl(
                user.getIdUsuario(),
                user.getEmail(),
                user.getContrasena(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * getPassword (OTP)
     * @return password
     */
    @Override
    public String getPassword() {
        return usuario.getContrasena();
    }

    @Override
    public String getUsername() {
        return "";
    }

    /**
     * getName
     * @return name
     */
    public String getName() {
        return usuario.getNombreCompleto();
    }

    /**
     * getEmail
     * @return email
     */
    public String getEmail() {
        return usuario.getEmail();
    }

    /**
     * isEnabled
     * @return if user is enabled
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * isAccountNonLocked
     * @return if user is locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * isAccountNonExpired
     * @return if account is not expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * isCredentialsNonExpired
     * @return if credential is not expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
