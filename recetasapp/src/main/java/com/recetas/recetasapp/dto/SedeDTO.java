package com.recetas.recetasapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SedeDTO {
    private Long idSede;
    private String nombreSede;
    private String direccionSede;
    private String telefonoSede;
    private String mailSede;
    private String whatsapp;
    private String tipoBonificacion;
    private Boolean bonificaCursos;
    private String tipoPromocion;
    private String promocionCursos;
    private String imagenUrl; 
}


