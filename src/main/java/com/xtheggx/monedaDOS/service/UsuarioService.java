package com.xtheggx.monedados.service;

import com.xtheggx.monedados.model.Usuario;
import java.util.List;

public interface UsuarioService {

    // CRUD operations for Usuario entity
    Usuario createUsuario(Usuario usuario);
    Usuario getUsuarioById(Long id);
    Usuario updateUsuario(Usuario usuario);
    void deleteUsuario(Long id);
    List<Usuario> getAllUsuarios();

}
