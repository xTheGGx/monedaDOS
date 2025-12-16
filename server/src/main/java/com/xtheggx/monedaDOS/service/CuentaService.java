package com.xtheggx.monedaDOS.service;

import com.xtheggx.monedaDOS.dto.CuentaDTO;
import com.xtheggx.monedaDOS.dto.CuentaResponse;
import com.xtheggx.monedaDOS.dto.UpdateCuentaDTO;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {

    List<CuentaResponse> listar(Long usuarioId);

    BigDecimal saldoTotal(Long usuarioId);

    CuentaResponse crear(Long usuarioId, CuentaDTO dto);

    CuentaResponse actualizar(Long cuentaId, Long usuarioId, UpdateCuentaDTO dto);

    void eliminar(Long cuentaId, Long usuarioId);
}
