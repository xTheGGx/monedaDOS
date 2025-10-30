package com.xtheggx.monedaDOS.service.impl;

import com.xtheggx.monedaDOS.dto.CuentaDTO;
import com.xtheggx.monedaDOS.exception.ResourceNotFoundException;
import com.xtheggx.monedaDOS.model.Cuenta;
import com.xtheggx.monedaDOS.model.CuentaTipo;
import com.xtheggx.monedaDOS.repository.CuentaRepository;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import com.xtheggx.monedaDOS.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CuentaServiceImpl implements CuentaService {
    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    @Override
    public Cuenta buscarPorIdCuenta(Integer id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta con id " + id + " no encontrada"));
    }

    @Override
    public Cuenta crearCuenta(CuentaDTO nuevaCuenta) {
        // Verificar existencia del usuario
        var usuario = usuarioRepository.findById(nuevaCuenta.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario con id " + nuevaCuenta.getUsuarioId() + " no existe"));

        // Evitar duplicado de nombre de cuenta para el mismo usuario
        if (cuentaRepository.existsByNombreAndUsuarioIdUsuario(nuevaCuenta.getNombre(), nuevaCuenta.getUsuarioId())) {
            throw new DataIntegrityViolationException(
                    "El usuario ya tiene una cuenta con nombre '" + nuevaCuenta.getNombre() + "'");
        }

        // Mapear DTO a entidad Cuenta
        Cuenta cuenta = new Cuenta();
        cuenta.setUsuario(usuario);
        cuenta.setNombre(nuevaCuenta.getNombre());
        cuenta.setTipo(nuevaCuenta.getTipo());
        // Si no especificó moneda, asignar "MXN" por defecto
        cuenta.setMoneda(
                (nuevaCuenta.getMoneda() == null || nuevaCuenta.getMoneda().isEmpty())
                        ? "MXN"
                        : nuevaCuenta.getMoneda()
        );
        cuenta.setDiaCorte(nuevaCuenta.getDiaCorte());
        cuenta.setDiaPago(nuevaCuenta.getDiaPago());
        cuenta.setLimiteCredito(nuevaCuenta.getLimiteCredito());
        // Saldo inicial: si el DTO no lo maneja, inicia en 0.00 (ya es default en entidad)
        cuenta.setSaldo(BigDecimal.ZERO);
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);

        // Nota: el dueño se añade a la relación cuentasCompartidas en Usuario automáticamente vía trigger SQL inicial.
        // Si se quisiera asegurar en JPA, habría que añadir la cuenta a usuario.getCuentasCompartidas() y guardar usuario.

        return cuentaGuardada;
    }

    @Override
    public Cuenta actualizarCuenta(Integer id, CuentaDTO CuentaDTO) {
        Cuenta existente = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta con id " + id + " no encontrada para actualizar"));

        // Si el cliente desea cambiar de usuario propietario, obtener el nuevo usuario (opcionalmente podríamos no permitir cambiar propietario)
        if (CuentaDTO.getUsuarioId() != null &&
                !existente.getUsuario().getIdUsuario().equals(CuentaDTO.getUsuarioId())) {
            var nuevoUsuario = usuarioRepository.findById(CuentaDTO.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuario con id " + CuentaDTO.getUsuarioId() + " no existe"));
            existente.setUsuario(nuevoUsuario);
        }
        // Actualizar campos básicos (si vienen en DTO)
        if (CuentaDTO.getNombre() != null) {
            // verificar duplicado nombre si cambió
            if (!existente.getNombre().equals(CuentaDTO.getNombre()) &&
                    cuentaRepository.existsByNombreAndUsuarioIdUsuario(CuentaDTO.getNombre(), existente.getUsuario().getIdUsuario())) {
                throw new DataIntegrityViolationException(
                        "El usuario ya tiene una cuenta con nombre '" + CuentaDTO.getNombre() + "'");
            }
            existente.setNombre(CuentaDTO.getNombre());
        }
        if (CuentaDTO.getTipo() != null) {
            existente.setTipo(CuentaDTO.getTipo());
        }
        if (CuentaDTO.getMoneda() != null && !CuentaDTO.getMoneda().isEmpty()) {
            existente.setMoneda(CuentaDTO.getMoneda());
        }
        // Campos opcionales
        existente.setDiaCorte(CuentaDTO.getDiaCorte());   // puede ser null
        existente.setDiaPago(CuentaDTO.getDiaPago());
        existente.setLimiteCredito(CuentaDTO.getLimiteCredito());

        // *No* se actualiza el saldo aquí directamente; el saldo cambia solo por transacciones.
        // Guardar cambios:
        return cuentaRepository.save(existente);
    }

    @Override
    public Cuenta actualizarParcial(Integer id, Map<String, Object> cambios) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta con id " + id + " no encontrada para actualización parcial"));

        // Iterar sobre las claves y aplicar cambios soportados
        for (Map.Entry<String, Object> entry : cambios.entrySet()) {
            String campo = entry.getKey();
            Object valor = entry.getValue();
            switch (campo) {
                case "nombre":
                    if (valor instanceof String nombreNuevo && !nombreNuevo.isBlank()) {
                        if (!cuenta.getNombre().equals(nombreNuevo) &&
                                cuentaRepository.existsByNombreAndUsuarioIdUsuario(nombreNuevo, cuenta.getUsuario().getIdUsuario())) {
                            throw new DataIntegrityViolationException(
                                    "Ya existe otra cuenta con nombre '" + nombreNuevo + "' para el usuario");
                        }
                        cuenta.setNombre(nombreNuevo);
                    }
                    break;
                case "tipo":
                    if (valor instanceof String tipoStr) {
                        // Convertir string a enum TipoCuenta
                        CuentaTipo tipoNuevo = CuentaTipo.valueOf(tipoStr);
                        cuenta.setTipo(tipoNuevo);
                    }
                    break;
                case "moneda":
                    if (valor instanceof String mon && mon.length() == 3) {
                        cuenta.setMoneda(mon);
                    }
                    break;
                case "diaCorte":
                    if (valor instanceof Number num) {
                        int dia = num.intValue();
                        if (dia >= 1 && dia <= 31) {
                            cuenta.setDiaCorte(dia);
                        } else {
                            throw new IllegalArgumentException("Dia de corte debe estar entre 1 y 31");
                        }
                    }
                    break;
                case "diaPago":
                    if (valor instanceof Number num) {
                        int dia = num.intValue();
                        if (dia >= 1 && dia <= 31) {
                            cuenta.setDiaPago(dia);
                        } else {
                            throw new IllegalArgumentException("Dia de pago debe estar entre 1 y 31");
                        }
                    }
                    break;
                case "limiteCredito":
                    if (valor instanceof Number num) {
                        BigDecimal limite = new BigDecimal(num.toString());
                        if (limite.compareTo(BigDecimal.ZERO) < 0) {
                            throw new IllegalArgumentException("El límite de crédito no puede ser negativo");
                        }
                        cuenta.setLimiteCredito(limite);
                    }
                    break;
                // Se ignoran campos no reconocidos o no permitidos (usuarioId, saldo no deben cambiarse aquí)
                default:
                    // Podemos omitir cambios en campos no soportados
                    break;
            }
        }
        // Guardar y devolver cuenta actualizada
        return cuentaRepository.save(cuenta);
    }

    @Override
    public void eliminarCuenta(Integer id) {
        // Verificar existencia
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta con id " + id + " no encontrada para eliminar"));
        // Intentar eliminar
        cuentaRepository.delete(cuenta);
        // Si existe violación de integridad (ej. cuenta con transacciones), se lanzará DataIntegrityViolationException automáticamente.
    }




}


/*

Lo deje en el miunuto >2>41>27
 */