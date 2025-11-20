// src/main/java/mx/dgtic/sfp/repository/UsuarioRepository.java
package com.xtheggx.monedaDOS.repository;

import com.xtheggx.monedaDOS.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmailIgnoreCase(String email);

    Usuario findByIdUsuario(Long idUsuario);

    @Query("SELECT u.idUsuario FROM Usuario u WHERE u.email = :email")
    Long getUsuarioIdUsuarioByEmail(@Param("email") String email);

    Long findIdUsuarioByEmail(String email);

    @Query("select u.idUsuario from Usuario u where upper(u.email) = upper(:email)")
    Optional<Long> findIdByEmailIgnoreCase(@Param("email") String email);

    @Query("select u.idUsuario from Usuario u where upper(u.email) = upper(:email)")
    Usuario findByEmailIgnoreCase(String email);

}
