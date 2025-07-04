package com.recetas.recetasapp.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComentarioResponse {
    private Long idComentario;
    private String nombreUsuario;
    private String texto;
    private LocalDateTime fechaCreacion;
}
