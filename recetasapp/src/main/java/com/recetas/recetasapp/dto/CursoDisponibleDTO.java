package com.recetas.recetasapp.dto;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CursoDisponibleDTO {
    private Long idCurso;
    private String nombre;
    private String descripcion;
    private String contenidos;
    private String requerimientos;
    private String duracion;
    private Double precio;
    private String modalidad;
    private String imagenUrl;
    private String nivel;
    private String chef;
}
