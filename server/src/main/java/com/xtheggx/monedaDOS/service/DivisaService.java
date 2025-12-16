package com.xtheggx.monedaDOS.service;

import com.xtheggx.monedaDOS.model.Divisa;

import java.util.List;

public interface DivisaService  {

    List<Divisa> listarDivisas(Long userId);
}
