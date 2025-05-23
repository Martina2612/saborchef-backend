package com.recetas.recetasapp.dto;

import lombok.Data;

@Data
public class UtilizadoDTO {
    private Long idIngrediente;
    private Double cantidad;
    private Long idUnidad;
    private String observaciones;
}
