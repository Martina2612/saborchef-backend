package com.recetas.recetasapp.dto.request;

import lombok.Data;

@Data
public class CalificacionRequest {
    private Long idUsuario; // ID del usuario que califica
    private Long idReceta;
    private Integer calificacion; // de 1 a 5
}
