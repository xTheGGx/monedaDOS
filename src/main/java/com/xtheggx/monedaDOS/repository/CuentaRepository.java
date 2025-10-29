package com.xtheggx.monedaDOS.repository;

import com.xtheggx.monedaDOS.model.Cuenta;
import com.xtheggx.monedaDOS.model.CuentaTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {
    List<Cuenta> findByUsuarioIdUsuario(Long usuarioIdUsuario);
    List<Cuenta> findByTipo(CuentaTipo tipo);

    boolean existsByUsuarioIdUsuario(Long usuarioIdUsuario);

    boolean existsByNombreAndUsuarioIdUsuario(String nombre, Long usuarioIdUsuario);
}