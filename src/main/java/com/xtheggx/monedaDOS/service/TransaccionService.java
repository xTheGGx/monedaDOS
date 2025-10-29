package com.xtheggx.monedaDOS.service;

import com.xtheggx.monedaDOS.dto.TransaccionDTO;
import com.xtheggx.monedaDOS.model.CategoriaTipo;
import com.xtheggx.monedaDOS.model.Clasificacion;
import com.xtheggx.monedaDOS.model.Transaccion;

import java.util.List;

public interface TransaccionService {

    List<Transaccion> listarTransacciones();
    Transaccion buscarPorId(Integer id);
    Transaccion registrarTransaccion(TransaccionDTO nuevaTransaccion);
    Transaccion actualizarTransaccion(Integer id, TransaccionDTO TransaccionDTO);
    Transaccion actualizarParcial(Integer id, java.util.Map<String, Object> cambios);
    void eliminarTransaccion(Integer id);

    // Filtrar por cuenta, tipo y clasificación
    List<Transaccion> filtrarPorCuentaTipoYClasificacion(Integer cuentaId, CategoriaTipo tipo, Clasificacion clasif);
    List<Transaccion> filtrarPorCuentaYTipo(Integer cuentaId, CategoriaTipo tipo);
    List<Transaccion> filtrarPorCuentaYClasificacion(Integer cuentaId, Clasificacion clasif);
    List<Transaccion> filtrarPorTipoYClasificacion(CategoriaTipo tipo, Clasificacion clasif);
    List<Transaccion> filtrarPorCuenta(Integer cuentaId);
    List<Transaccion> filtrarPorTipo(CategoriaTipo tipo);
    List<Transaccion> filtrarPorClasificacion(Clasificacion clasif);


}
