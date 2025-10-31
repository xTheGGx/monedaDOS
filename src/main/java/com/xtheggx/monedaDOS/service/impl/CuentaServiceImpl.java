package com.xtheggx.monedaDOS.service.impl;

import com.xtheggx.monedaDOS.dto.CuentaDTO;
import com.xtheggx.monedaDOS.model.Cuenta;
import com.xtheggx.monedaDOS.model.Usuario;
import com.xtheggx.monedaDOS.repository.CuentaRepository;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import com.xtheggx.monedaDOS.service.CuentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository repo;
    private final UsuarioRepository usuarioRepo;

    @Override
    public List<Cuenta> listar(Long userId) {
        log.debug("CuentaService.listarPorUsuario userId={}", userId);
        return repo.findAllByUsuarioIdUsuario(userId);
    }

    @Override
    public BigDecimal saldoTotal(Long userId) {
        log.debug("CuentaService.saldoTotal userId={}", userId);
        return repo.sumSaldoByUsuarioIdUsuario(userId); // o sumar en memoria si no tienes query
    }

    @Transactional
    @Override
    public void crear(Long userId, CuentaDTO dto) {
        log.debug("CuentaService.crear dto={} userId={}", dto, userId);

        Cuenta c = new Cuenta();
        Usuario userCuenta = usuarioRepo.findByIdUsuario(userId);
        c.setUsuario(userCuenta);
        c.setNombre(dto.getNombre());
        c.setTipo(dto.getTipo());

        // Defaults indispensables
        c.setSaldo(BigDecimal.ZERO);
        c.setMoneda("MXN");

        // Solo aplica para crédito; para otros tipos deja null
        if (dto.getTipo() != null && dto.getTipo().name().equals("CREDITO")) {
            c.setDiaCorte(dto.getDiaCorte());
            c.setDiaPago(dto.getDiaPago());
            c.setLimiteCredito(dto.getLimiteCredito());
        } else {
            c.setDiaCorte(null);
            c.setDiaPago(null);
            c.setLimiteCredito(null);
        }

        Cuenta saved = repo.save(c);
        log.info("Cuenta creada idCuenta={} userId={}", saved.getIdCuenta(), userId);
    }
}
