package com.recetas.recetasapp.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class RecetaCrearRequest {
    private Long idUsuario;
    private String nombreReceta;
    private String descripcionReceta;
    private String fotoPrincipal;
    private Integer cantidadPersonas;
    private String tipo; // nombre de la categoría (enum Categoria)
    private List<IngredienteCantidad> ingredientes;
    private List<PasoCrear> pasos;
    private List<FotoCrear> fotos;

    @Data
    public static class IngredienteCantidad {
        private String nombreIngrediente; // si no existe, se crea
        private Double cantidad;
        private String unidad; // debe ser uno de kg, gr, ml, litros, unidad
        private String observaciones;
    }

    @Data
    public static class PasoCrear {
        private Integer nroPaso;
        private String texto;
        private List<MultimediaCrear> contenidos;
    }

    @Data
    public static class MultimediaCrear {
        private String tipoContenido;
        private String extension;
        private String urlContenido;
    }

    @Data
    public static class FotoCrear {
        private String urlFoto;
        private String descripcion;
    }
}