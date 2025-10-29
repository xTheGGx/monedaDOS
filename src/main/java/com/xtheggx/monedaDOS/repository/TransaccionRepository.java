package com.xtheggx.monedaDOS.repository;

import com.xtheggx.monedaDOS.model.CategoriaTipo;
import com.xtheggx.monedaDOS.model.Clasificacion;
import com.xtheggx.monedaDOS.model.Transaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {

    List<Transaccion> findByCuentaIdCuenta(Integer cuentaId);
    List<Transaccion> findByCategoriaTipo(CategoriaTipo tipo);
    List<Transaccion> findByCategoriaClasificacion(Clasificacion clasificacion);
    List<Transaccion> findByCuentaIdCuentaAndCategoriaTipo(Integer cuentaId, CategoriaTipo tipo);
    List<Transaccion> findByCuentaIdCuentaAndCategoriaClasificacion(Integer cuentaId, Clasificacion clasificacion);
    List<Transaccion> findByCategoriaTipoAndCategoriaClasificacion(CategoriaTipo tipo, Clasificacion clasificacion);
    List<Transaccion> findByCuentaIdCuentaAndCategoriaTipoAndCategoriaClasificacion(Integer cuentaId, CategoriaTipo tipo, Clasificacion clasificacion);

    // Página por usuario
    @Query("SELECT t FROM Transaccion t WHERE t.usuario.idUsuario = :usuarioId")
    Page<Transaccion> pageByUsuario(@Param("usuarioId") Integer usuarioId, Pageable pageable);

    // Página por usuario + rango de fechas
    @Query("SELECT t FROM Transaccion t " +
            "WHERE t.usuario.idUsuario = :usuarioId " +
            "AND t.fecha BETWEEN :from AND :to")
    Page<Transaccion> pageByUsuarioAndFechaBetween(@Param("usuarioId") Integer usuarioId,
                                                   @Param("from") LocalDateTime from,
                                                   @Param("to") LocalDateTime to,
                                                   Pageable pageable);

    // (Opcional) si quieres orden por defecto en la query, normalmente basta con Pageable.sort
    // pero si lo prefieres explícito:
    @Query("SELECT t FROM Transaccion t WHERE t.usuario.idUsuario = :usuarioId")
    Page<Transaccion> pageByUsuarioOrderByFecha(@Param("usuarioId") Integer usuarioId, Pageable pageable);
}
