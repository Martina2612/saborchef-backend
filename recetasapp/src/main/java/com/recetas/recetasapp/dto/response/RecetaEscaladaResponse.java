package com.recetas.recetasapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.recetas.recetasapp.dto.request.IngredienteCantidadDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecetaEscaladaResponse {
    private Long idRecetaOriginal;
    private String nombreReceta;
    private String nombreUsuario;     // Autor de la receta
    private String tipoReceta;        // Nombre del tipo de receta
    private String descripcionReceta;  // Descripción original (no cambia)
    private Integer porcionesOriginal; // Cantidad de porciones original
    private Integer porcionesEscaladas; // Cantidad de porciones escaladas calculadas
    private Double factorEscalado;     // Factor usado para escalar (por ejemplo 0.5, 2.0, etc.)
    private List<IngredienteCantidadDTO> ingredientes; // Ingredientes con cantidades escaladas
}