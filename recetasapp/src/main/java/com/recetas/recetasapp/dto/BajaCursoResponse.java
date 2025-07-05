package com.recetas.recetasapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BajaCursoResponse {
    private String mensaje;
    private Double montoReintegro;
    private Double porcentajeReintegro;
    private Double precioOriginal;
    private String tipoReintegro; 
}
