package com.recetas.recetasapp.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaseDTO {
    private Long idClase;
    private String titulo;
    private String descripcion;
    private Integer numeroClase;
    private Date fechaClase;
}
