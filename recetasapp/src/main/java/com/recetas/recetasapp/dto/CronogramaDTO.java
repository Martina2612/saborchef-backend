package com.recetas.recetasapp.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CronogramaDTO {
    private Date fechaInicio;
    private Date fechaFin;
    private Integer vacantesDisponibles;
    private SedeDTO sede;
}
