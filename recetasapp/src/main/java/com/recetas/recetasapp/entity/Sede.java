package com.recetas.recetasapp.entity;

import jakarta.persistence.Entity;
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
public class Sede {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSede;

    private String nombreSede;
    private String direccionSede;
    private String telefonoSede;
    private String mailSede;
    private String whatsapp;
    private String tipoBonificacion;
    private Boolean bonificaCursos;
    private String tipoPromocion;
    private String promocionCursos;
}

