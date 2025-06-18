package com.recetas.recetasapp.entity;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

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
    @JsonBackReference
    private Curso curso;

    private Date fechaInicio;
    private Date fechaFin;
    private Integer vacantesDisponibles;

    @OneToMany(mappedBy = "cronograma", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InscripcionCurso> inscripciones;

    @JsonIgnore
    public List<Alumno> getAlumnosInscritos() {
        if (inscripciones == null) return List.of(); // evitar null pointer
        return inscripciones.stream()
                .map(InscripcionCurso::getAlumno)
                .collect(Collectors.toList());
    }
}
