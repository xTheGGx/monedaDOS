package com.xtheggx.monedaDOS.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.xtheggx.monedaDOS.model.CuentaTipo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {

    @NotNull(message = "El id de usuario (propietario) es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El nombre de la cuenta es obligatorio")
    @Size(max = 100, message = "El nombre de la cuenta debe tener máximo 100 caracteres")
    private String nombre;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private CuentaTipo tipo;

    @Size(min = 3, max = 3, message = "La moneda debe especificarse con código ISO 3 letras")
    private String moneda;

    @Min(value = 1, message = "Día de corte debe ser entre 1 y 31")
    @Max(value = 31, message = "Día de corte debe ser entre 1 y 31")
    private Integer diaCorte;

    @Min(value = 1, message = "Día de pago debe ser entre 1 y 31")
    @Max(value = 31, message = "Día de pago debe ser entre 1 y 31")
    private Integer diaPago;

    @DecimalMin(value = "0.00", inclusive = true, message = "El límite de crédito no puede ser negativo")
    private java.math.BigDecimal limiteCredito;



}
