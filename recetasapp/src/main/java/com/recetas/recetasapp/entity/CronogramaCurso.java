package com.recetas.recetasapp.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CronogramaCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCronograma;

    @ManyToOne
    private Sede sede;

    @ManyToOne
    private Curso curso;

    @Column
    private Date fechaInicio;
    @Column
    private Date fechaFin;
    @Column
    private Integer vacantesDisponibles;

    @Column
    List<Alumno> alumnos;
}

