package com.xtheggx.monedaDOS.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 80, message = "El nombre de la categoría debe tener máximo 80 caracteres")
    private String nombre;

    @NotNull(message = "El tipo de categoría es obligatorio")
    private com.xtheggx.monedaDOS.model.CategoriaTipo tipo;

    // Clasificación es opcional (puede ser null)
    private com.xtheggx.monedaDOS.model.Clasificacion clasificacion;

    @NotBlank(message = "El color de la categoría es obligatorio")
    private String color;
}
