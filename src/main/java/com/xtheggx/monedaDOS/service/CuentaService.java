package com.xtheggx.monedados.service;

import com.xtheggx.monedados.dto.CuentaDTO;
import com.xtheggx.monedados.model.Cuenta;

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
