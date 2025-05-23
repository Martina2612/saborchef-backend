package com.recetas.recetasapp.entity;

import java.sql.Date;

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

    private Date fechaInicio;
    private Date fechaFin;
    private Integer vacantesDisponibles;
}

