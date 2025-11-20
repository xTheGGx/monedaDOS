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
import java.util.Arrays;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {

    @Query("SELECT t FROM Transaccion t " +
            "WHERE t.usuario.idUsuario = :userId ORDER BY t.fecha DESC")
    List<Transaccion> findAllByUser(@Param("userId") Long userId);

    @Query("SELECT t FROM Transaccion t " +
            "WHERE t.usuario.idUsuario = :userId AND t.cuenta.idCuenta = :cuentaId " +
            "ORDER BY t.fecha DESC")
    List<Transaccion> findByUserAndCuenta(@Param("userId") Long userId,
                                          @Param("cuentaId") Integer cuentaId);

    @Query("SELECT t FROM Transaccion t " +
            "WHERE t.usuario.idUsuario = :userId AND t.categoria.idCategoria = :catId " +
            "ORDER BY t.fecha DESC")
    List<Transaccion> findByUserAndCategoria(@Param("userId") Long userId,
                                             @Param("catId") Integer categoriaId);

    @Query("SELECT t FROM Transaccion t " +
            "WHERE t.usuario.idUsuario = :userId AND t.categoria.tipo = :tipo " +
            "ORDER BY t.fecha DESC")
    List<Transaccion> findByUserAndTipo(@Param("userId") Long userId,
                                        @Param("tipo") CategoriaTipo tipo);


    // Listados completos (sin paginar)
    List<Transaccion> findByUsuarioIdUsuarioOrderByFechaDesc(Integer userId);

    List<Transaccion> findByUsuarioIdUsuarioAndFechaBetweenOrderByFechaDesc(Integer userId,
                                                                            LocalDateTime from,
                                                                            LocalDateTime to);

    // Paginados
    Page<Transaccion> findByUsuarioIdUsuarioOrderByFechaDesc(Integer userId, Pageable pageable);

    Page<Transaccion> findByUsuarioIdUsuarioAndFechaBetweenOrderByFechaDesc(Integer userId,
                                                                            LocalDateTime from,
                                                                            LocalDateTime to,
                                                                            Pageable pageable);

    List<Transaccion> findByCategoriaTipoAndCategoriaClasificacion(CategoriaTipo tipo, Clasificacion clasificacion);
}
