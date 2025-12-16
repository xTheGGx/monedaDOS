package com.xtheggx.monedaDOS.repository;

import com.xtheggx.monedaDOS.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    List<Cuenta> findAllByUsuarioIdUsuario(Long usuarioIdUsuario);

    @Query("select coalesce(sum(c.saldo),0) from Cuenta c where c.usuario.idUsuario = :uid")
    BigDecimal sumSaldoByUsuarioIdUsuario(@Param("uid") Long usuarioId);

    boolean existsByUsuarioIdUsuarioAndNombre(Long usuarioIdUsuario, String nombre);
}