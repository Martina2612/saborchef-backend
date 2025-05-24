package com.recetas.recetasapp.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "recetas")
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReceta;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    private String nombreReceta;

    @Column(length = 1000)
    private String descripcionReceta;

    private String fotoPrincipal;
    private String porciones;
    private Integer cantidadPersonas;

    @ManyToOne
    @JoinColumn(name = "idTipo")
    private TipoReceta tipo;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<Pasos> pasos;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<Utilizado> utilizados;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<Foto> fotos;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<Calificacion> calificaciones;

    @Column
    private LocalDateTime fechaCreacion;

    @Column
    private boolean habilitada = false;

    // Getters y Setters
}
