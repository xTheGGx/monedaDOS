package com.xtheggx.monedaDOS.service;

import com.xtheggx.monedaDOS.dto.CuentaDTO;
import com.xtheggx.monedaDOS.model.Cuenta;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface CuentaService {

    List<Cuenta> listar(Long usuarioId);

    BigDecimal saldoTotal(Long usuarioId);

    void crear(Long usuarioId, CuentaDTO f);
}
