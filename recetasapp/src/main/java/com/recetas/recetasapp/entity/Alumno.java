package com.recetas.recetasapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "alumnos")
@Data
public class Alumno {

    @Id
    @Column(name = "id_alumno")
    private Long idAlumno;  // MapsId tomará el valor de Usuario.id

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_alumno")
    private Usuario usuario;

    // Datos de pago
    @Column(name = "numero_tarjeta", length = 30)
    private String numeroTarjeta;

    @Column(name = "tipo_tarjeta", length = 20)
    private String tipoTarjeta;        // ej. Visa, Mastercard

    @Column(name = "vencimiento", length = 7)
    private String vencimiento;        // formato MM/AA

    @Column(name = "codigo_seguridad", length = 5)
    private String codigoSeguridad;    // CVV

    // Datos de DNI
    @Lob
    @Column(name = "dni_frente")
    private String dniFrente;

    @Lob
    @Column(name = "dni_dorso")
    private String dniDorso;


    @Column(name = "numero_tramite", length = 50)
    private String numeroTramite;      // número de trámite
}
