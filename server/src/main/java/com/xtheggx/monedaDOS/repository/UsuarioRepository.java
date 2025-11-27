package com.xtheggx.monedaDOS.repository;

import com.xtheggx.monedaDOS.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // 1. Verificar si existe
    boolean existsByEmailIgnoreCase(String email);

    // 2. Buscar usuario completo
    Optional<Usuario> findByEmailIgnoreCase(String email);

    // 3. Buscar SOLO el ID 
    @Query("SELECT u.idUsuario FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<Long> findIdByEmailIgnoreCase(@Param("email") String email);

}