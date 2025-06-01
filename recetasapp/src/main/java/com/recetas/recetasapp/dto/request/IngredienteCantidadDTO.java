package com.recetas.recetasapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredienteCantidadDTO {

    private Long idIngrediente;
    private String nombreIngrediente;
    private Double cantidad;       // Cantidad escalada
    private String unidadDescripcion; // Ej: "kg", "gr", etc.
    private String observaciones;  // Si existen (opcional)
}
