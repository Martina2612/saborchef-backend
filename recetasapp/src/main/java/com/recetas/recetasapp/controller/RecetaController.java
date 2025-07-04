package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.request.RecetaCrearRequest;
import com.recetas.recetasapp.dto.request.RecetaFiltroRequest;
import com.recetas.recetasapp.dto.response.RecetaDetalleResponse;
import com.recetas.recetasapp.dto.response.RecetaResumenResponse;
import com.recetas.recetasapp.entity.Receta;
import com.recetas.recetasapp.repository.RecetaRepository;
import com.recetas.recetasapp.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    @Autowired
    private RecetaRepository recetaRepository;

    @GetMapping
public ResponseEntity<List<RecetaResumenResponse>> listarRecetas(
        @RequestParam(name = "idUsuario", required = false) Long idUsuario,
        @RequestParam(name = "idTipo", required = false) Long idTipo,
        @RequestParam(name = "orden", required = false) String orden) {
    return ResponseEntity.ok(recetaService.listarRecetas(idUsuario, idTipo, orden));
}


    @PostMapping
    public ResponseEntity<Void> crearReceta(@RequestBody RecetaCrearRequest request) {
        recetaService.crearReceta(request);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaDetalleResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(recetaService.obtenerReceta(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody RecetaCrearRequest request) {
        recetaService.actualizarReceta(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recetaService.eliminarReceta(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecetaDetalleResponse>> buscarPorNombre(@RequestParam String nombre, @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarPorNombre(nombre, orden));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<RecetaDetalleResponse>> buscarPorTipo(@PathVariable String tipo, @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarPorTipo(tipo, orden));
    }

    @GetMapping("/ingrediente")
    public ResponseEntity<List<RecetaDetalleResponse>> buscarPorIngrediente(@RequestParam String nombreIngrediente, @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarPorIngrediente(nombreIngrediente, orden));
    }

    @GetMapping("/sin-ingrediente")
    public ResponseEntity<List<RecetaDetalleResponse>> buscarSinIngrediente(@RequestParam String nombreIngrediente, @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarSinIngrediente(nombreIngrediente, orden));
    }

    @GetMapping("/buscar/usuario")
    public ResponseEntity<List<RecetaDetalleResponse>> buscarPorUsuario(
            @RequestParam String nombre,
            @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarPorUsuario(nombre, orden));
    }

    /**
     * GET /api/recetas/usuario/{id}
     * Devuelve las recetas subidas por el usuario con el ID dado.
     */
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<RecetaDetalleResponse>> obtenerPorUsuario(
            @PathVariable("id") Long usuarioId,
            @RequestParam(name = "orden", defaultValue = "fechaDesc") String orden
    ) {
        List<RecetaDetalleResponse> lista =
            recetaService.buscarPorUsuarioId(usuarioId, orden);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/ultimas3")
    public List<RecetaDetalleResponse> obtenerUltimas3Recetas() {
        return recetaService.obtenerUltimas3Recetas();
    }

    // Endpoint para buscar recetas por filtros
    @PostMapping("/buscar")
    public ResponseEntity<List<RecetaDetalleResponse>> buscarPorFiltros(@RequestBody RecetaFiltroRequest filtro) {
        List<RecetaDetalleResponse> resultados = recetaService.buscarPorFiltros(filtro);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/ultimas")
        public ResponseEntity<List<RecetaDetalleResponse>> obtenerUltimasRecetas() {
            List<RecetaDetalleResponse> ultimas = recetaService.obtenerUltimasRecetas();
            return ResponseEntity.ok(ultimas);
        }

    /**
     * Endpoint para sugerencias de autocompletado:
     * /recetas/suggestions?prefix=pi
     */
    @GetMapping("/suggestions")
    public List<String> sugerirNombres(@RequestParam String prefix) {
        return recetaRepository.findTop10ByNombreRecetaStartingWith(prefix);
    }

}