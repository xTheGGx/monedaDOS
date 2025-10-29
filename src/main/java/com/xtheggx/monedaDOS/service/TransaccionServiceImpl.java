package com.xtheggx.monedaDOS.service;

import com.xtheggx.monedaDOS.dto.TransaccionDTO;
import com.xtheggx.monedaDOS.exception.ResourceNotFoundException;
import com.xtheggx.monedaDOS.model.Categoria;
import com.xtheggx.monedaDOS.model.CategoriaTipo;
import com.xtheggx.monedaDOS.model.Clasificacion;
import com.xtheggx.monedaDOS.model.Transaccion;
import com.xtheggx.monedaDOS.repository.CategoriaRepository;
import com.xtheggx.monedaDOS.repository.CuentaRepository;
import com.xtheggx.monedaDOS.repository.TransaccionRepository;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TransaccionServiceImpl implements  TransaccionService{
    @Autowired
    private TransaccionRepository transaccionRepo;
    @Autowired
    private CuentaRepository cuentaRepo;
    @Autowired
    private CategoriaRepository categoriaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    public List<Transaccion> listarTransacciones() {
        return transaccionRepo.findAll();
    }

    @Override
    public Transaccion buscarPorId(Integer id) {
        return transaccionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transacción con id " + id + " no encontrada"));
    }

    @Override
    public Transaccion registrarTransaccion(TransaccionDTO nuevaTx) {
        // Verificar existencia de usuario, cuenta y categoría
        var usuario = usuarioRepo.findById(nuevaTx.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario con id " + nuevaTx.getUsuarioId() + " no existe"));
        var cuenta = cuentaRepo.findById(nuevaTx.getCuentaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta con id " + nuevaTx.getCuentaId() + " no existe"));
        var categoria = categoriaRepo.findById(nuevaTx.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría con id " + nuevaTx.getCategoriaId() + " no existe"));

        // REGLA DE NEGOCIO: validar signo del monto según tipo de categoría
        BigDecimal monto = nuevaTx.getMonto();
        CategoriaTipo tipoCat = categoria.getTipo();
        if ((tipoCat == CategoriaTipo.EGRESO && monto.compareTo(BigDecimal.ZERO) >= 0) ||
                (tipoCat == CategoriaTipo.INGRESO && monto.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException(
                    "El monto debe ser negativo para egresos y positivo para ingresos (categoría "
                            + categoria.getNombre() + ")");
        }

        // Mapear DTO a entidad Transaccion
        Transaccion tx = new Transaccion();
        tx.setUsuario(usuario);
        tx.setCuenta(cuenta);
        tx.setCategoria(categoria);
        tx.setFecha(LocalDateTime.now());  // usar fecha actual; si se quisiera una fecha específica, se agregaría en DTO
        tx.setMonto(monto);
        tx.setDescripcion(nuevaTx.getDescripcion());

        Transaccion guardada = transaccionRepo.save(tx);
        // Los triggers en la BD ajustarán el saldo de la cuenta automáticamente después de insertar.
        // Opcional: podríamos refrescar la entidad Cuenta si quisiéramos el saldo actualizado inmediatamente.
        return guardada;
    }

    @Override
    public Transaccion actualizarTransaccion(Integer id, TransaccionDTO txDto) {
        Transaccion existente = transaccionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transacción con id " + id + " no encontrada para actualizar"));

        // Actualizar los campos con la info del DTO (tratamos como reemplazo completo)
        // Puede implicar cambiar de cuenta o categoría, lo cual afecta el saldo vía triggers.

        // Verificar y asignar nuevo usuario si difiere
        if (!existente.getUsuario().getIdUsuario().equals(txDto.getUsuarioId())) {
            var nuevoUsuario = usuarioRepo.findById(txDto.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuario con id " + txDto.getUsuarioId() + " no existe"));
            existente.setUsuario(nuevoUsuario);
        }
        // Verificar y asignar nueva cuenta si difiere
        if (!existente.getCuenta().getIdCuenta().equals(txDto.getCuentaId())) {
            var nuevaCuenta = cuentaRepo.findById(txDto.getCuentaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cuenta con id " + txDto.getCuentaId() + " no existe"));
            existente.setCuenta(nuevaCuenta);
        }
        // Verificar y asignar nueva categoría si difiere
        if (!existente.getCategoria().getIdCategoria().equals(txDto.getCategoriaId())) {
            var nuevaCat = categoriaRepo.findById(txDto.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Categoría con id " + txDto.getCategoriaId() + " no existe"));
            existente.setCategoria(nuevaCat);
        }
        // Actualizar monto y descripción
        BigDecimal montoNuevo = txDto.getMonto();
        existente.setDescripcion(txDto.getDescripcion());

        // Validar signo con la categoría (posiblemente actualizada)
        CategoriaTipo tipoCat = existente.getCategoria().getTipo();
        if ((tipoCat == CategoriaTipo.EGRESO && montoNuevo.compareTo(BigDecimal.ZERO) >= 0) ||
                (tipoCat == CategoriaTipo.INGRESO && montoNuevo.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException(
                    "El monto debe ser negativo para egreso y positivo para ingreso según categoría seleccionada");
        }
        existente.setMonto(montoNuevo);

        // Actualizar fecha? Podríamos permitir actualizar la fecha si viene en DTO (no está en TransaccionDTO actualmente).
        // Por simplicidad mantenemos la fecha original.

        // Guardar cambios
        return transaccionRepo.save(existente);
        // Los triggers manejarán los ajustes de saldo correspondientes si cuenta o monto cambiaron.
    }

    @Override
    public Transaccion actualizarParcial(Integer id, Map<String, Object> cambios) {
        Transaccion tx = transaccionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transacción con id " + id + " no encontrada para actualización parcial"));

        // Guardar valores originales para posibles validaciones cruzadas
        Categoria categoriaOriginal = tx.getCategoria();
        BigDecimal montoOriginal = tx.getMonto();

        // Flags para saber si cambió algo relevante para validación
        boolean categoriaCambio = false;
        boolean montoCambio = false;

        for (Map.Entry<String, Object> entry : cambios.entrySet()) {
            String campo = entry.getKey();
            Object valor = entry.getValue();
            switch(campo) {
                case "cuentaId":
                    if (valor instanceof Number num) {
                        int nuevaCuentaId = num.intValue();
                        if (!tx.getCuenta().getIdCuenta().equals(nuevaCuentaId)) {
                            var nuevaCuenta = cuentaRepo.findById(nuevaCuentaId)
                                    .orElseThrow(() -> new ResourceNotFoundException(
                                            "Cuenta con id " + nuevaCuentaId + " no existe"));
                            tx.setCuenta(nuevaCuenta);
                        }
                    }
                    break;
                case "categoriaId":
                    if (valor instanceof Number num) {
                        int nuevaCatId = num.intValue();
                        if (!tx.getCategoria().getIdCategoria().equals(nuevaCatId)) {
                            var nuevaCategoria = categoriaRepo.findById(nuevaCatId)
                                    .orElseThrow(() -> new ResourceNotFoundException(
                                            "Categoría con id " + nuevaCatId + " no existe"));
                            tx.setCategoria(nuevaCategoria);
                            categoriaCambio = true;
                        }
                    }
                    break;
                case "usuarioId":
                    if (valor instanceof Number num) {
                        Long nuevoUsuarioId = num.longValue();
                        if (!tx.getUsuario().getIdUsuario().equals(nuevoUsuarioId)) {
                            var nuevoUsuario = usuarioRepo.findById(nuevoUsuarioId)
                                    .orElseThrow(() -> new ResourceNotFoundException(
                                            "Usuario con id " + nuevoUsuarioId + " no existe"));
                            tx.setUsuario(nuevoUsuario);
                        }
                    }
                    break;
                case "monto":
                    if (valor instanceof Number num) {
                        // Convertir número a BigDecimal correctamente
                        BigDecimal nuevoMonto = new BigDecimal(num.toString());
                        tx.setMonto(nuevoMonto);
                        montoCambio = true;
                    }
                    break;
                case "descripcion":
                    if (valor instanceof String desc) {
                        tx.setDescripcion(desc);
                    }
                    break;
                case "fecha":
                    // Si se quisiera permitir cambio de fecha, implementar parseo de fecha desde String.
                    // En este caso, ignoramos cambios en fecha para no complicar.
                    break;
                default:
                    // Ignorar campos desconocidos
                    break;
            }
        }

        // Validar signo solo si cambió categoría o monto (o ambos)
        if (categoriaCambio || montoCambio) {
            Categoria cat = tx.getCategoria();
            CategoriaTipo tipoCat = cat.getTipo();
            BigDecimal montoValidar = tx.getMonto();  // monto posiblemente actualizado
            if ((tipoCat == CategoriaTipo.EGRESO && montoValidar.compareTo(BigDecimal.ZERO) >= 0) ||
                    (tipoCat == CategoriaTipo.INGRESO && montoValidar.compareTo(BigDecimal.ZERO) <= 0)) {
                throw new IllegalArgumentException(
                        "El monto debe ser negativo para egresos y positivo para ingresos (categoría "
                                + cat.getNombre() + ")");
            }
        }

        // Guardar transacción con cambios
        return transaccionRepo.save(tx);
    }

    @Override
    public void eliminarTransaccion(Integer id) {
        Transaccion tx = transaccionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transacción con id " + id + " no encontrada para eliminar"));
        transaccionRepo.delete(tx);
        // Los triggers en BD ajustarán el saldo de la cuenta restando el monto eliminado.
    }

    @Override
    public List<Transaccion> filtrarPorCuentaTipoYClasificacion(Integer cuentaId, CategoriaTipo tipo, Clasificacion clasif) {
        return transaccionRepo.findByCuentaIdCuentaAndCategoriaTipoAndCategoriaClasificacion(cuentaId, tipo, clasif);
    }

    public List<Transaccion> filtrarPorCuenta(Integer cuentaId) {
        return transaccionRepo.findByCuentaIdCuenta(cuentaId);
    }
    public List<Transaccion> filtrarPorTipo(CategoriaTipo tipo) {
        return transaccionRepo.findByCategoriaTipo(tipo);
    }
    public List<Transaccion> filtrarPorClasificacion(Clasificacion clasif) {
        return transaccionRepo.findByCategoriaClasificacion(clasif);
    }
    public List<Transaccion> filtrarPorCuentaYTipo(Integer cuentaId, CategoriaTipo tipo) {
        return transaccionRepo.findByCuentaIdCuentaAndCategoriaTipo(cuentaId, tipo);
    }

    @Override
    public List<Transaccion> filtrarPorCuentaYClasificacion(Integer cuentaId, Clasificacion clasif) {
        return transaccionRepo.findByCuentaIdCuentaAndCategoriaClasificacion(cuentaId, clasif);
    }

    @Override
    public List<Transaccion> filtrarPorTipoYClasificacion(CategoriaTipo tipo, Clasificacion clasif) {
        return transaccionRepo.findByCategoriaTipoAndCategoriaClasificacion(tipo, clasif);
    }

}
