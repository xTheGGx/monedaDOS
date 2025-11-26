package com.xtheggx.monedaDOS.service.impl;

import com.xtheggx.monedaDOS.dto.TransaccionDTO;
import com.xtheggx.monedaDOS.exception.ResourceNotFoundException;
import com.xtheggx.monedaDOS.model.*;
import com.xtheggx.monedaDOS.repository.CategoriaRepository;
import com.xtheggx.monedaDOS.repository.CuentaRepository;
import com.xtheggx.monedaDOS.repository.TransaccionRepository;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import com.xtheggx.monedaDOS.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transRepo;
    private final CategoriaRepository catRepo;
    private final CuentaRepository cuentaRepo;
    private final UsuarioRepository usuarioRepo;

    @Override
    public List<Transaccion> listar(Long userId, Long cuentaId, Long categoriaId, String tipo) {
        log.debug("TransaccionService.listar userId={} cuentaId={} categoriaId={} tipo={}", userId, cuentaId, categoriaId, tipo);
        if (cuentaId != null) return transRepo.findByUserAndCuenta(userId, cuentaId);
        if (categoriaId != null) return transRepo.findByUserAndCategoria(userId, categoriaId);
        if (tipo != null && !tipo.isBlank()) return transRepo.findByUserAndTipo(userId, CategoriaTipo.valueOf(tipo));
        return transRepo.findAllByUser(userId);
    }

    @Transactional
    @Override
    public Transaccion crear(Long userId, TransaccionDTO dto) {
        log.info("TransaccionService.crear userId={} dto={}", userId, dto);

        Usuario u = usuarioRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Cuenta cta = cuentaRepo.findById(dto.getCuentaId()).orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        if (!cta.getUsuario().getIdUsuario().equals(userId))
            throw new IllegalArgumentException("La cuenta no pertenece al usuario");

        Categoria cat = catRepo.findById(dto.getCategoriaId()).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        Transaccion t = new Transaccion();
        t.setUsuario(u);
        t.setCuenta(cta);
        t.setCategoria(cat);
        t.setMonto(dto.getMonto());
        t.setDescripcion(dto.getDescripcion());
        t.setFecha(LocalDateTime.now());

        Transaccion saved = transRepo.save(t);
        log.info("Transacción creada id={} monto={} tipo={}", saved.getIdTransaccion(), saved.getMonto(), cat.getTipo());

        // Ajuste de saldo según tipo de categoría
        BigDecimal saldo = cta.getSaldo() == null ? BigDecimal.ZERO : cta.getSaldo();
        saldo = (cat.getTipo() == CategoriaTipo.INGRESO) ? saldo.add(dto.getMonto()) : saldo.subtract(dto.getMonto());
        cta.setSaldo(saldo);
        cuentaRepo.save(cta);
        log.debug("Saldo de cuenta {} actualizado a {}", cta.getIdCuenta(), saldo);

        return saved;
    }

    @Transactional
    @Override
    public void eliminar(Long transaccionId) {
        log.info("TransaccionService.eliminar  transaccionId={}", transaccionId);
        Transaccion t = transRepo.findById(transaccionId).orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));

        Cuenta cta = t.getCuenta();
        BigDecimal saldo = cta.getSaldo() == null ? BigDecimal.ZERO : cta.getSaldo();
        saldo = (t.getCategoria().getTipo() == CategoriaTipo.INGRESO) ? saldo.subtract(t.getMonto()) : saldo.add(t.getMonto());
        cta.setSaldo(saldo);
        cuentaRepo.save(cta);

        transRepo.delete(t);
        log.info("Transacción {} eliminada. Saldo cuenta {} => {}", transaccionId, cta.getIdCuenta(), saldo);
    }

    @Transactional
    @Override
    public Categoria crearCategoria(Long userId, String nombre, String tipo) {
        log.info("TransaccionService.crearCategoria userId={} nombre={} tipo={}", userId, nombre, tipo);
        Usuario u = usuarioRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Categoria c = new Categoria();
        c.setUsuario(u);
        c.setNombre(nombre);
        c.setTipo(CategoriaTipo.valueOf(tipo));
        c.setClasificacion(Clasificacion.NECESIDAD);

        Categoria saved = catRepo.save(c);
        log.info("Categoría creada id={} nombre={}", saved.getIdCategoria(), saved.getNombre());
        return saved;
    }


    private BigDecimal normalizeMonto(BigDecimal monto, CategoriaTipo tipo) {
        if (monto == null) {
            throw new IllegalArgumentException("El monto es obligatorio");
        }
        BigDecimal abs = monto.abs();
        return (tipo == CategoriaTipo.EGRESO) ? abs.negate() : abs; // EGRESO => negativo, INGRESO => positivo
    }

    private void validarPropiedadCuenta(Cuenta cuenta, Long userId) {
        if (cuenta == null) throw new ResourceNotFoundException("Cuenta no encontrada");
        if (cuenta.getUsuario() == null || !cuenta.getUsuario().getIdUsuario().equals(userId)) {
            throw new IllegalArgumentException("La cuenta no pertenece al usuario");
        }
    }

    private void validarPropiedadCategoria(Categoria categoria, Long userId) {
        if (categoria == null) throw new ResourceNotFoundException("Categoría no encontrada");
        // Categoría puede ser global (usuario == null) o del usuario
        if (categoria.getUsuario() != null && !categoria.getUsuario().getIdUsuario().equals(userId)) {
            throw new IllegalArgumentException("La categoría no pertenece al usuario");
        }
    }

    // ---------- Implementaciones solicitadas ----------

    @Override
    @Transactional(readOnly = true)
    public List<Transaccion> listarTransaccionesPorUsuarioYFechas(Long userId, LocalDateTime from, LocalDateTime to) {
        if (userId == null) throw new IllegalArgumentException("userId obligatorio");
        if (from == null || to == null) throw new IllegalArgumentException("Rango de fechas obligatorio");
        return transRepo.findByUsuarioIdUsuarioAndFechaBetweenOrderByFechaDesc(userId.longValue(), from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaccion> listarTransaccionesPorUsuario(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId obligatorio");
        return transRepo.findByUsuarioIdUsuarioOrderByFechaDesc(userId.longValue());
    }

    @Override
    public void registrarTransaccion(@Valid TransaccionDTO dto) {
        if (dto == null) throw new IllegalArgumentException("TransaccionDTO es obligatorio");
        if (dto.getUsuarioId() == null) throw new IllegalArgumentException("usuarioId es obligatorio");

        // 1) Cargar cuenta y categoría y validar pertenencia
        Cuenta cuenta = cuentaRepo.findById(dto.getCuentaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
        validarPropiedadCuenta(cuenta, dto.getUsuarioId());

        Categoria categoria = catRepo.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        validarPropiedadCategoria(categoria, dto.getUsuarioId());

        // 2) Normalizar monto según tipo de la categoría
        BigDecimal montoNormalizado = normalizeMonto(dto.getMonto(), categoria.getTipo());

        // 3) Construir entidad
        Transaccion tx = new Transaccion();
        tx.setUsuario(cuenta.getUsuario());        // mismo usuario de la cuenta
        tx.setCuenta(cuenta);
        tx.setCategoria(categoria);
        tx.setMonto(montoNormalizado);
        tx.setDescripcion(dto.getDescripcion());
        tx.setFecha(LocalDateTime.now());          

        // 4) Guardar
        transRepo.save(tx);
    }

    @Override
    public void actualizarTransaccion(Long idTransaccion, @Valid TransaccionDTO dto) {
        if (idTransaccion == null) throw new IllegalArgumentException("idTransaccion es obligatorio");
        if (dto == null) throw new IllegalArgumentException("TransaccionDTO es obligatorio");
        if (dto.getUsuarioId() == null) throw new IllegalArgumentException("usuarioId es obligatorio");

        // 1) Cargar transacción
        Transaccion existente = transRepo.findById(idTransaccion)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));

        // 2) Validar que la transacción pertenezca al usuario
        if (existente.getUsuario() == null || !existente.getUsuario().getIdUsuario().equals(dto.getUsuarioId())) {
            throw new IllegalArgumentException("No tienes permiso para editar esta transacción");
        }

        // 3) Cargar y validar cuenta/categoría (si vienen en DTO)
        Cuenta cuenta = cuentaRepo.findById(dto.getCuentaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
        validarPropiedadCuenta(cuenta, dto.getUsuarioId());

        Categoria categoria = catRepo.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        validarPropiedadCategoria(categoria, dto.getUsuarioId());

        // 4) Normalizar monto según tipo
        BigDecimal montoNormalizado = normalizeMonto(dto.getMonto(), categoria.getTipo());

        // 5) Actualizar campos (conserva fecha original)
        existente.setCuenta(cuenta);
        existente.setCategoria(categoria);
        existente.setMonto(montoNormalizado);
        existente.setDescripcion(dto.getDescripcion());

        // 6) Guardar
        transRepo.save(existente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaccion> listarTransaccionesPaginadas(Long userId, int page, int pageSize) {
        if (userId == null) throw new IllegalArgumentException("userId obligatorio");
        PageRequest pr = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "fecha"));
        return transRepo.findByUsuarioIdUsuarioOrderByFechaDesc(userId.longValue(), pr);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaccion> listarTransaccionesPaginadasPorFecha(Long userId, LocalDateTime from, LocalDateTime to, int page, int pageSize) {
        if (userId == null) throw new IllegalArgumentException("userId obligatorio");
        if (from == null || to == null) throw new IllegalArgumentException("Rango de fechas obligatorio");
        PageRequest pr = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "fecha"));
        return transRepo.findByUsuarioIdUsuarioAndFechaBetweenOrderByFechaDesc(userId.longValue(), from, to, pr);
    }
}
