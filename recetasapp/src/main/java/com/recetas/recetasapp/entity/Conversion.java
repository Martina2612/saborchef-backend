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
public class Conversion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConversion;

    @ManyToOne
    private Unidad unidadOrigen;

    @ManyToOne
    private Unidad unidadDestino;

    @Column
    private Double factorConversiones;
}
