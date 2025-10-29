// src/main/java/mx/dgtic/sfp/repository/UsuarioRepository.java
package com.xtheggx.monedaDOS.repository;

import com.xtheggx.monedaDOS.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    boolean existsByEmailIgnoreCase(String email);
    Usuario findByEmailIgnoreCase(String email);
}
