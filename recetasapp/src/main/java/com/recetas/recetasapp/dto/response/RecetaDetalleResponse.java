package com.recetas.recetasapp.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RecetaDetalleResponse {
    private Long idReceta;
    private String nombre;
    private String descripcion;
    private String fotoPrincipal;
    private Integer porciones;
    private String tipo;
    private String nombreUsuario;
    private List<IngredienteDetalle> ingredientes;
    private List<PasoDetalle> pasos;
    private List<String> fotos;
    private List<ComentarioResponse> comentarios;
    private Double promedioCalificacion;
    private Integer duracion;

    @Data
    public static class IngredienteDetalle {
        private String nombre;
        private Double cantidad;
        private String unidad;
        private String observaciones;
    }

    @Data
    public static class PasoDetalle {
        private Integer nroPaso;
        private String texto;
        private List<Contenido> contenidos;
    }

    @Data
    public static class Contenido {
        private String tipo;
        private String extension;
        private String url;
    }

    @Data
    public static class ComentarioResponse {
        private String nombreUsuario;
        private String texto;
    }

}