package com.recetas.recetasapp.dto;

import lombok.Data;

@Data
public class RecetaResumenDTO {
    private Long idReceta;
    private String nombreReceta;
    private String fotoPrincipal;
    private String tipoDescripcion;
}
