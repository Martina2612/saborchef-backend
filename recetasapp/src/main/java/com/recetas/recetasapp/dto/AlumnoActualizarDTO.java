package com.recetas.recetasapp.dto;

import lombok.Data;

@Data
public class AlumnoActualizarDTO {
    private String numeroTarjeta;
    private String dniFrente; // path a la imagen, o base64 si estás enviando eso
    private String dniDorso;
    private String cuentaCorriente;
    private String nombre; // puede ser redundante si ya lo tiene Usuario
}

