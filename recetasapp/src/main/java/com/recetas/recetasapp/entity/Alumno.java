package com.recetas.recetasapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "alumnos")
@Data
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlumno;

    @OneToOne
    @JoinColumn(name = "idUsuario", referencedColumnName = "id")
    private Usuario usuario;

    private String numeroTarjeta;
    private String dniFrente;
    private String dniDorso;
    private String nombre;
    private String cuentaCorriente;
}
