package com.recetas.recetasapp.dto.request;

import lombok.Data;

@Data
public class ComentarioRequest {
    private Long idReceta;
    private String texto;
}
