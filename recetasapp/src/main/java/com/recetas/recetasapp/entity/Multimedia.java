package com.recetas.recetasapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "multimedia")
public class Multimedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContenido;

    @ManyToOne
    @JoinColumn(name = "idPaso", nullable = false)
    private Pasos paso;

    @Column
    private String tipoContenido;
    
    @Column
    private String extension;

    @Column(columnDefinition = "TEXT")
    private String urlContenido;
}

