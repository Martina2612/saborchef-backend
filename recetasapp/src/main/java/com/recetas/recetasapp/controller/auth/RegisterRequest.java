package com.recetas.recetasapp.controller.auth;

import com.recetas.recetasapp.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    // Datos del usuario
    private String nombre;
    private String apellido;
    private String alias;
    private String email;
    private String password;
    private Rol role;

    // Datos adicionales solo si es alumno
    private String numeroTarjeta;
    private String tipoTarjeta;
    private String vencimiento;
    private String codigoSeguridad;

    private String dniFrente;      // base64
    private String dniDorso;       // base64
    private String numeroTramite;
}
