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
    @MapsId
    @JoinColumn(name = "idAlumno")
    private Usuario usuario;

    private String numeroTarjeta;
    private String dniFrente;
    private String dniDorso;
    private String cuentaCorriente;
}
