package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.response.RecetaEscaladaResponse;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.service.RecetaService;
import com.recetas.recetasapp.service.UsuarioService;  
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Controlador específico para manejar el “escalado” de recetas y el guardado de las mismas.
 */
@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
public class RecetaEscaladoController {

    private RecetaService recetaService;
    private UsuarioService usuarioService; 
    // Asumo que tienes algún UsuarioService que te permite:
    // 1) Obtener el Usuario autenticado (para guardar recetas)
    // 2) O buscar Usuario por id, etc.

    /**
     * 1) Escalar por factor:
     *    - factor = 0.5 => la mitad
     *    - factor = 2.0 => el doble
     */
    @GetMapping("/{id}/escalar")
    public ResponseEntity<RecetaEscaladaResponse> escalarPorFactor(
            @PathVariable("id") Long idReceta,
            @RequestParam("factor") Double factor) {
        try {
            RecetaEscaladaResponse resp = recetaService.escalarRecetaPorFactor(idReceta, factor);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 2) Escalar por porciones deseadas:
     *    - porcionesDeseadas = 4 => calcula factor = 4 / porcionesOriginal
     */
    @GetMapping("/{id}/escalar/porciones")
    public ResponseEntity<RecetaEscaladaResponse> escalarPorPorciones(
            @PathVariable("id") Long idReceta,
            @RequestParam("porcionesDeseadas") @Min(1) Integer porcionesDeseadas) {
        try {
            RecetaEscaladaResponse resp = recetaService.escalarRecetaPorPorciones(idReceta, porcionesDeseadas);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 3) Escalar por cantidad de un ingrediente:
     *    - ingredienteId = 7, nuevaCantidad = 200 => factor = 200 / cantidadOriginalDelIngrediente(7)
     */
    @GetMapping("/{id}/escalar/porIngrediente")
    public ResponseEntity<RecetaEscaladaResponse> escalarPorIngrediente(
            @PathVariable("id") Long idReceta,
            @RequestParam("ingredienteId") Long ingredienteId,
            @RequestParam("cantidad") Double cantidad) {
        try {
            RecetaEscaladaResponse resp = recetaService.escalarRecetaPorIngrediente(idReceta, ingredienteId, cantidad);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 4) Guardar (persistir) una versión escalada de la receta. 
     *    Solo se puede guardar hasta 10 recetas escaladas por usuario.
     *
     *    Parámetros posibles (mutuamente excluyentes):
     *      - factor=2.0                           → guarda la receta al doble de cantidades.
     *      - porcionesDeseadas=8                  → calcula factor internamente.
     *      - ingredienteId & cantidad              → calcula factor internamente.
     *
     *    El usuario se obtiene del JWT (alias) mediante @AuthenticationPrincipal.
     */
    @PostMapping("/{id}/guardar")
    public ResponseEntity<Void> guardarRecetaEscalada(
            @PathVariable("id") Long idReceta,
            @RequestParam(value = "factor", required = false) Double factor,
            @RequestParam(value = "porcionesDeseadas", required = false) Integer porcionesDeseadas,
            @RequestParam(value = "ingredienteId", required = false) Long ingredienteId,
            @RequestParam(value = "cantidad", required = false) Double cantidad,
            @AuthenticationPrincipal UserDetails userDetails   // Usuario autenticado vía JWT
    ) {
        try {
            // 1) Verificar que venga un UserDetails válido (requiere autenticación)
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }

            // 2) Obtener la entidad Usuario a partir del alias (username) del JWT
            String alias = userDetails.getUsername();
            Usuario usuarioActual = usuarioService.getUserByAlias(alias);
            if (usuarioActual == null) {
                return ResponseEntity.status(401).build();
            }

            // 3) Determinar el factor final de escalado según el parámetro que venga
            Double factorFinal;
            if (factor != null) {
                factorFinal = factor;
            } else if (porcionesDeseadas != null) {
                RecetaEscaladaResponse resp = recetaService.escalarRecetaPorPorciones(idReceta, porcionesDeseadas);
                factorFinal = resp.getFactorEscalado();
            } else if (ingredienteId != null && cantidad != null) {
                RecetaEscaladaResponse resp = recetaService.escalarRecetaPorIngrediente(idReceta, ingredienteId, cantidad);
                factorFinal = resp.getFactorEscalado();
            } else {
                // No vino ningún parámetro de escalado válido
                return ResponseEntity.badRequest().build();
            }

            // 4) Guardar la receta escalada (lanzará excepción si ya tiene 10 guardadas)
            recetaService.guardarRecetaEscalada(idReceta, usuarioActual, factorFinal);
            return ResponseEntity.status(201).build();

        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 5) Listar todas las recetas escaladas que el usuario ha guardado (hasta 10).
     *    GET /api/recetas/guardadas
     */
    @GetMapping("/guardadas")
    public ResponseEntity<List<RecetaEscaladaResponse>> listarGuardadas(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        String alias = userDetails.getUsername();
        Usuario usuarioActual = usuarioService.getUserByAlias(alias);
        if (usuarioActual == null) {
            return ResponseEntity.status(401).build();
        }

        List<RecetaEscaladaResponse> lista = recetaService.listarRecetasEscaladasGuardadas(usuarioActual);
        return ResponseEntity.ok(lista);
    }

    /**
     * 6) Eliminar una receta escalada guardada (por su id en la tabla recetas_guardadas).
     *    DELETE /api/recetas/guardadas/{idGuardada}
     */
    @DeleteMapping("/guardadas/{idGuardada}")
    public ResponseEntity<Void> eliminarGuardada(
            @PathVariable("idGuardada") Long idGuardada,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }
            String alias = userDetails.getUsername();
            Usuario usuarioActual = usuarioService.getUserByAlias(alias);
            if (usuarioActual == null) {
                return ResponseEntity.status(401).build();
            }

            recetaService.eliminarRecetaGuardada(idGuardada, usuarioActual);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}