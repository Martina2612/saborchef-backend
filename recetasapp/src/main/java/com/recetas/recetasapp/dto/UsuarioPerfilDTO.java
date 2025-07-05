package com.recetas.recetasapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioPerfilDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String alias;
    private String email;
    private String telefono;
    private String fotoPerfil;
}