// src/main/java/mx/dgtic/sfp/repository/UsuarioRepository.java
package com.xtheggx.monedaDOS.repository;

import com.xtheggx.monedaDOS.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Usuario findByEmailIgnoreCase(String email);

    Usuario findByIdUsuario(Long idUsuario);

    @Query("SELECT u.idUsuario FROM Usuario u WHERE u.email = :email")
    Long getUsuarioIdUsuarioByEmail(@Param("email") String email);
}
