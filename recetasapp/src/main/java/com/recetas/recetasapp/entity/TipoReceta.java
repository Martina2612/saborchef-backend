package com.recetas.recetasapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tipo_receta")
public class TipoReceta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipo;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private Categoria descripcion;
}

