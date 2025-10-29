// src/main/java/mx/dgtic/sfp/repository/RolRepository.java
package com.xtheggx.monedados.repository;

import com.xtheggx.monedados.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombreIgnoreCase(String nombre);
}
