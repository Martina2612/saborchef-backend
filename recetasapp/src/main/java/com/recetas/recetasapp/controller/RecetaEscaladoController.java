package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.response.RecetaEscaladaResponse;
import com.recetas.recetasapp.entity.Receta;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.exception.ResourceNotFoundException;
import com.recetas.recetasapp.repository.RecetaRepository;
import com.recetas.recetasapp.service.RecetaService;
import com.recetas.recetasapp.service.UsuarioService;  
import jakarta.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

/**
* Controlador específico para manejar el "escalado" de recetas y el guardado de las mismas.
*/
@RestController
@RequestMapping("/api/recetas")
public class RecetaEscaladoController {

   @Autowired
   private RecetaService recetaService;
   
   @Autowired
   private UsuarioService usuarioService;
   
   @Autowired
   private RecetaRepository recetaRepository;

   /**
    * 1) Escalar por factor:
    *    - factor = 0.5 => la mitad
    *    - factor = 2.0 => el doble
    */
   @GetMapping("/{id}/escalar")
   public ResponseEntity<Object> escalarPorFactor(
           @PathVariable("id") Long idReceta,
           @RequestParam("factor") Double factor) {
       
       // Log de entrada para debugging
       System.out.println("=== DEBUG ESCALADO ===");
       System.out.println("ID Receta: " + idReceta);
       System.out.println("Factor: " + factor);
       
       try {
           // Verificar que la receta existe primero
           Optional<Receta> recetaOpt = recetaRepository.findById(idReceta);
           if (recetaOpt.isEmpty()) {
               System.out.println("ERROR: Receta no encontrada con ID: " + idReceta);
               return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
           }
           
           Receta receta = recetaOpt.get();
           System.out.println("Receta encontrada: " + receta.getNombreReceta());
           System.out.println("Porciones originales: " + receta.getPorciones());
           
           // Verificar porciones válidas
           if (receta.getPorciones() == null || receta.getPorciones() <= 0) {
               String errorMsg = "La receta no tiene porciones válidas. Porciones: " + receta.getPorciones();
               System.out.println("ERROR: " + errorMsg);
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
           }
           
           // Verificar factor válido
           if (factor == null || factor <= 0) {
               String errorMsg = "Factor inválido: " + factor;
               System.out.println("ERROR: " + errorMsg);
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
           }
           
           System.out.println("Llamando a escalarRecetaPorFactor...");
           RecetaEscaladaResponse resp = recetaService.escalarRecetaPorFactor(idReceta, factor);
           System.out.println("Escalado exitoso");
           
           return ResponseEntity.status(HttpStatus.OK).body(resp);
           
       } catch (ResourceNotFoundException ex) {
           System.out.println("ERROR ResourceNotFoundException: " + ex.getMessage());
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
           
       } catch (IllegalArgumentException ex) {
           System.out.println("ERROR IllegalArgumentException: " + ex.getMessage());
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
           
       } catch (IllegalStateException ex) {
           System.out.println("ERROR IllegalStateException: " + ex.getMessage());
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
           
       } catch (Exception ex) {
           System.out.println("ERROR Exception inesperada: " + ex.getMessage());
           ex.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Error interno: " + ex.getMessage());
       }
   }

   // Endpoint para escalar por porciones deseadas (más intuitivo)
   @GetMapping("/{id}/escalar/porciones")
   public ResponseEntity<?> escalarPorPorciones(
           @PathVariable("id") Long idReceta,
           @RequestParam("porciones") Integer porcionesDeseadas) {
       try {
           RecetaEscaladaResponse resp = recetaService.escalarRecetaPorPorciones(idReceta, porcionesDeseadas);
           return ResponseEntity.ok(resp);
       } catch (ResourceNotFoundException ex) {
           return ResponseEntity.notFound().build();
       } catch (IllegalArgumentException | IllegalStateException ex) {
           System.err.println("Error de validación en escalado por porciones: " + ex.getMessage());
           return ResponseEntity.badRequest()
                   .body(Map.of("error", ex.getMessage()));
       } catch (Exception ex) {
           System.err.println("Error inesperado en escalado por porciones: " + ex.getMessage());
           ex.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(Map.of("error", "Error interno del servidor"));
       }
   }

   // Endpoint para escalar por ingrediente específico
   @GetMapping("/{id}/escalar/ingrediente")
   public ResponseEntity<?> escalarPorIngrediente(
           @PathVariable("id") Long idReceta,
           @RequestParam("ingredienteId") Long ingredienteId,
           @RequestParam("cantidad") Double nuevaCantidad) {
       try {
           RecetaEscaladaResponse resp = recetaService.escalarRecetaPorIngrediente(idReceta, ingredienteId, nuevaCantidad);
           return ResponseEntity.ok(resp);
       } catch (ResourceNotFoundException ex) {
           return ResponseEntity.notFound().build();
       } catch (IllegalArgumentException | IllegalStateException ex) {
           System.err.println("Error de validación en escalado por ingrediente: " + ex.getMessage());
           return ResponseEntity.badRequest()
                   .body(Map.of("error", ex.getMessage()));
       } catch (Exception ex) {
           System.err.println("Error inesperado en escalado por ingrediente: " + ex.getMessage());
           ex.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(Map.of("error", "Error interno del servidor"));
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