package com.recetas.recetasapp.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pasos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaso;

    private Integer nroPaso;

    @Column(length = 1000)
    private String texto;

    @ManyToOne
    private Receta receta;

    @OneToMany(mappedBy = "paso", cascade = CascadeType.ALL)
    private List<Multimedia> contenidos;
}

