package com.recetas.recetasapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recetas_editadas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecetaEditada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que guardó la receta escalada. Es una relación ManyToOne con Usuario.
     */
    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    /**
     * Receta original a la que se le aplicó el escalado. Es una relación 
     * ManyToOne con Receta.
     */
    @ManyToOne
    @JoinColumn(name = "idReceta", nullable = false)
    private Receta recetaOriginal;

    /**
     * Factor de escalado con el que se guardó. Por ejemplo 0.5, 2.0, etc.
     * Si se usó escalado por ingediente, también se almacena acá.
     */
    @Column(nullable = false)
    private Double factorEscalado;

    /**
     * Fecha en la que el usuario guardó esta receta escalada.
     */
    @Column(nullable = false)
    private LocalDateTime fechaGuardado;
}
