package com.recetas.recetasapp.dto;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CursoDisponibleDTO {
    private Long idCronograma;
    private String nombreSede;
    private String direccionSede;
    private String descripcionCurso;
    private String duracion;
    private Double precio;
    private Date fechaInicio;
    private Date fechaFin;
    private Integer vacantesDisponibles;
}