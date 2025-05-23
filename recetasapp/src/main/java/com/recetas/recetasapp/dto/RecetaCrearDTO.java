package com.recetas.recetasapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecetaCrearDTO {
    private Long idUsuario;
    private String nombreReceta;
    private String descripcionReceta;
    private String fotoPrincipal;
    private String porciones;
    private Integer cantidadPersonas;
    private Long idTipo;
    private List<PasoDTO> pasos;
    private List<UtilizadoDTO> utilizados;
    private List<String> fotosUrls;
}

