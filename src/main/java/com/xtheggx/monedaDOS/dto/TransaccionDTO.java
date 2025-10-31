package com.xtheggx.monedaDOS.dto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {

    @NotNull(message = "El id de la cuenta es obligatorio")
    private Integer cuentaId;

    @NotNull(message = "El id de la categoría es obligatorio")
    private Integer categoriaId;

    private Long usuarioId;

    @NotNull(message = "El monto de la transacción es obligatorio")
    @Digits(integer = 13, fraction = 2, message = "Formato de monto inválido")
    private BigDecimal monto;

    @Size(max = 255, message = "La descripción debe tener máximo 255 caracteres")
    private String descripcion;
}