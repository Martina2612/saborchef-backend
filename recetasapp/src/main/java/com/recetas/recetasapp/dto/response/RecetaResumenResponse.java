package com.recetas.recetasapp.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RecetaResumenResponse {
    private Long idReceta;
    private String nombre;
    private String fotoPrincipal;
    private Integer cantidadPersonas;
    private Double promedioCalificacion;
    private String tipo;
    private String nombreUsuario;
    private LocalDateTime fechaCreacion;
}
