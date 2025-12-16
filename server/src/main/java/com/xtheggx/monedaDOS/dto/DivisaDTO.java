package com.xtheggx.monedaDOS.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DivisaDTO {
    @NotNull(message = "La divisa es obligatoria")
    Long idDivisa;
    String codigoDivisa;
    String nombreDivisa;
}
