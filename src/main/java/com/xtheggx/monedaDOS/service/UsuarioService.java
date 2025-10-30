package com.xtheggx.monedaDOS.service;

import com.xtheggx.monedaDOS.dto.RegistroDTO;
import com.xtheggx.monedaDOS.model.Usuario;

import java.util.List;

public interface UsuarioService {

    // CRUD operations for Usuario entity
    Usuario createUsuario(Usuario usuario);

    Usuario getUsuarioById(Long id);

    Usuario updateUsuario(Usuario usuario);

    void deleteUsuario(Long id);

    List<Usuario> getAllUsuarios();

    /**
     * Proceso de negocio para registrar un nuevo usuario.
     * Se encarga de la conversión de DTO, encriptación y asignación de rol.
     */
    void registrarUsuario(RegistroDTO registroDTO);

}