package com.xtheggx.monedaDOS.service;

import com.xtheggx.monedaDOS.dto.CuentaDTO;
import com.xtheggx.monedaDOS.model.Cuenta;

import java.util.List;
import java.util.Map;


public interface CuentaService {
    List<Cuenta> listarCuentas();
    Cuenta buscarPorIdCuenta(Integer id);
    Cuenta crearCuenta(CuentaDTO cuenta);
    Cuenta actualizarParcial(Integer id, Map<String, Object> cambios);

    Cuenta actualizarCuenta(Integer id, CuentaDTO CuentaDTO);

    void eliminarCuenta(Integer id);
}
