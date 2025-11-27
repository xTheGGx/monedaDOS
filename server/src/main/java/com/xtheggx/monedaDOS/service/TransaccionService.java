package com.xtheggx.monedaDOS.service;

import com.xtheggx.monedaDOS.dto.TransaccionDTO;
import com.xtheggx.monedaDOS.model.Categoria;
import com.xtheggx.monedaDOS.model.Transaccion;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface TransaccionService {
    List<Transaccion> listar(Long userId, Long cuentaId, Long categoriaId, String tipo /*INGRESO|EGRESO*/);

    Transaccion crear(Long userId, TransaccionDTO dto);

    void eliminar(Long transaccionId);

    Categoria crearCategoria(Long userId, String nombre, String tipo /*INGRESO|EGRESO*/);

    List<Transaccion> listarTransaccionesPorUsuarioYFechas(Long userId, LocalDateTime from, LocalDateTime to);

    List<Transaccion> listarTransaccionesPorUsuario(Long userId);

    void registrarTransaccion(@Valid TransaccionDTO transaccionDTO);

    void actualizarTransaccion(Long idTransaccion, @Valid TransaccionDTO transaccionDTO);

    Page<Transaccion> listarTransaccionesPaginadas(Long userId, int page, int pageSize);

    Page<Transaccion> listarTransaccionesPaginadasPorFecha(Long userId, LocalDateTime from, LocalDateTime to, int page, int pageSize);
}
