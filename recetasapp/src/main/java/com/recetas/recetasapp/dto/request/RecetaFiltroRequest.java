package com.recetas.recetasapp.dto.request;

import java.util.List;
import lombok.Data;

@Data
public class RecetaFiltroRequest {
    private String nombre;
    private List<String> usuario;
    private List<String> tipo;
    private List<String> ingredientesIncluidos;
    private List<String> ingredientesExcluidos;
    private String orden; // Ej: "nombre_asc", "popularidad", etc.

    // Getters y setters
}
