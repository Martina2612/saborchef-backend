package com.recetas.recetasapp.dto;

import lombok.Data;

@Data
public class RegistroConfirmarDTO {
    private String mail;
    private String codigo;
    private String password;
    private String nombre;
    private String apellido;
}
