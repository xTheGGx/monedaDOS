package com.xtheggx.monedaDOS.service.impl;

import com.xtheggx.monedaDOS.model.Divisa;
import com.xtheggx.monedaDOS.repository.DivisaRepository;
import com.xtheggx.monedaDOS.service.DivisaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DivisaServiceImpl implements DivisaService {
    private final DivisaRepository divisaRepository;

    @Override
    public List<Divisa> listarDivisas(Long userId) {
        return divisaRepository.findAll();
    }
}
