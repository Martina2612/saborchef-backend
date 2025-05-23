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
public class AsistenciaCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsistencia;

    @ManyToOne
    private Alumno alumno;

    @ManyToOne
    private CronogramaCurso cronograma;

    private Date fecha;
}

