package com.recetas.recetasapp.controller.auth;

import com.recetas.recetasapp.entity.Rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String nombre;
    private String apellido;
    private String alias;

    private String email;

    private String password;

    private Rol role;
}
