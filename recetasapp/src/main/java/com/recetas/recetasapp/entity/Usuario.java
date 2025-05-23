package com.recetas.recetasapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    @Column(unique = true)
    private String nickname;

    @Email
    @NotBlank
    @Column(unique = true)
    private String mail;

    @NotBlank
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Rol rol;

    private Boolean habilitado=false;

}
