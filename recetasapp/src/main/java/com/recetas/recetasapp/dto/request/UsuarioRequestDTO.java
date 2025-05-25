package com.recetas.recetasapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    
    private String nombre;

    
    private String apellido;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
