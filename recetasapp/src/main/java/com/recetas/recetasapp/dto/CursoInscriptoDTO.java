package com.recetas.recetasapp.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CursoInscriptoDTO {
    private Long idCurso;
    private String nombreCurso;
    private String descripcion;
    private String modalidad;
    private String imagenUrl;
    private String nivel;
    private Double precio;
    private String duracion;     
    private String chef; 
    private Date fechaInicio;
    private Date fechaFin;
    private Long idCronograma;
    private SedeDTO sede;
    private float progreso;
    private boolean finalizado;
    
}

