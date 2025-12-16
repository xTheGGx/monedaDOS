package com.xtheggx.monedaDOS.service;

import com.xtheggx.monedaDOS.dto.CuentaDTO;
import com.xtheggx.monedaDOS.dto.CuentaResponse;
import com.xtheggx.monedaDOS.dto.UpdateCuentaDTO;
import com.xtheggx.monedaDOS.exception.GlobalExceptionHandler.ConflictException;
import com.xtheggx.monedaDOS.model.Cuenta;
import com.xtheggx.monedaDOS.model.Divisa;
import com.xtheggx.monedaDOS.model.Usuario;
import com.xtheggx.monedaDOS.repository.CuentaRepository;
import com.xtheggx.monedaDOS.repository.DivisaRepository;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DivisaRepository divisaRepository;

    @Override
    public List<CuentaResponse> listar(Long usuarioId) {
        List<Cuenta> cuentas = cuentaRepository.findAllByUsuarioIdUsuario(usuarioId);
        return cuentas.stream()
                .map(CuentaResponse::fromEntity)
                .toList();
    }

    @Override
    public BigDecimal saldoTotal(Long usuarioId) {
        return cuentaRepository.findById(usuarioId).stream()
                .map(Cuenta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public CuentaResponse crear(Long usuarioId, CuentaDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Divisa divisa = divisaRepository.findById(dto.getDivisaId())
                .orElseThrow(() -> new IllegalArgumentException("Divisa no encontrada"));

        String nombre = dto.getNombre().trim();

        if(cuentaRepository.existsByUsuarioIdUsuarioAndNombre(usuarioId, nombre)){
            throw new ConflictException("Ya existe una cuenta con ese nombre");
        }

        Cuenta cuenta = new Cuenta();
        cuenta.setUsuario(usuario);
        cuenta.setDivisa(divisa);
        cuenta.setNombre(dto.getNombre());
        cuenta.setTipo(dto.getTipo());
        cuenta.setSaldo(dto.getSaldoInicial() != null ? dto.getSaldoInicial() : BigDecimal.ZERO);
        cuenta.setDiaCorte(dto.getDiaCorte());
        cuenta.setDiaPago(dto.getDiaPago());
        cuenta.setLimiteCredito(dto.getLimiteCredito());

        Cuenta guardada = cuentaRepository.save(cuenta);
        return CuentaResponse.fromEntity(guardada);
    }

    @Override
    public CuentaResponse actualizar(Long cuentaId, Long usuarioId, UpdateCuentaDTO dto) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        if (!cuenta.getUsuario().getIdUsuario().equals(usuarioId)) {
            throw new AccessDeniedException("La cuenta no pertenece al usuario autenticado");
        }

        Divisa divisa = divisaRepository.findById(dto.getDivisaId())
                .orElseThrow(() -> new IllegalArgumentException("Divisa no encontrada"));

        cuenta.setNombre(dto.getNombre());
        cuenta.setDivisa(divisa);
        cuenta.setTipo(dto.getTipo());
        cuenta.setDiaCorte(dto.getDiaCorte());
        cuenta.setDiaPago(dto.getDiaPago());
        cuenta.setLimiteCredito(dto.getLimiteCredito());

        Cuenta guardada = cuentaRepository.save(cuenta);
        return CuentaResponse.fromEntity(guardada);
    }

    @Override
    public void eliminar(Long cuentaId, Long usuarioId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        if (!cuenta.getUsuario().getIdUsuario().equals(usuarioId)) {
            throw new AccessDeniedException("La cuenta no pertenece al usuario autenticado");
        }

        cuentaRepository.delete(cuenta);
    }
}
