// TopRecetaResponse.java
package com.recetas.recetasapp.dto.response;

import lombok.Data;

@Data
public class TopRecetaResponse {
    private Long idReceta;
    private String nombreReceta;
    private String fotoPrincipal;
    private Double promedioCalificacion;
}
