package com.recetas.recetasapp.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecetaDetalleDTO {
    private Long idReceta;
    private String nombreReceta;
    private String descripcionReceta;
    private String fotoPrincipal;
    private String porciones;
    private Integer cantidadPersonas;
    private String tipoDescripcion;
    private List<PasoDTO> pasos;
    private List<UtilizadoDTO> utilizados;
    private List<String> fotosUrls;
}
