// src/main/java/mx/dgtic/sfp/repository/UsuarioRepository.java
package com.xtheggx.monedados.repository;

import com.xtheggx.monedados.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Usuario findByEmailIgnoreCase(String email);
}
