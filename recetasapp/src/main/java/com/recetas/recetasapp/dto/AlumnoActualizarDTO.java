package com.recetas.recetasapp.dto;

import lombok.Data;

@Data
public class AlumnoActualizarDTO {
    // Medio de pago
    private String numeroTarjeta;
    private String tipoTarjeta;
    private String vencimiento;
    private String codigoSeguridad;

    // DNI
    private String dniFrente;     // aquí puede ser URL, base64 o nombre de archivo
    private String dniDorso;
    private String numeroTramite;
}
