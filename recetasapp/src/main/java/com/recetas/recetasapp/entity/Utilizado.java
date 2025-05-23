package com.recetas.recetasapp.entity;

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
public class Utilizado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUtilizado;

    @ManyToOne
    private Ingrediente ingrediente;

    @Column
    private Double cantidad;

    @ManyToOne
    private Unidad unidad;

    @Column
    private String observaciones;

    @ManyToOne
    private Receta receta;
}

