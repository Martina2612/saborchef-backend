package com.recetas.recetasapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCurso;
    private String nombre;	
    private String descripcion;
    private String contenidos;
    private String requerimientos;
    private String duracion;
    private Double precio;
    private String modalidad;
    private String imagenUrl;
    @Enumerated(EnumType.STRING)
    private Nivel nivel;
    private String chef;

}

