package com.recetas.recetasapp.dto.request;

import lombok.Data;

@Data
public class CalificacionRequest {
    private Long idReceta;
    private Integer calificacion; // de 1 a 5
}
