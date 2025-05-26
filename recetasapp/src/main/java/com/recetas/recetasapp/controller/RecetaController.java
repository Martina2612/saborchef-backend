package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.request.RecetaCrearRequest;
import com.recetas.recetasapp.dto.request.RecetaFiltroRequest;
import com.recetas.recetasapp.dto.response.RecetaDetalleResponse;
import com.recetas.recetasapp.dto.response.RecetaResumenResponse;
import com.recetas.recetasapp.entity.Receta;
import com.recetas.recetasapp.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    @GetMapping
    public ResponseEntity<List<RecetaResumenResponse>> listarRecetas(
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long idTipo,
            @RequestParam(required = false) String orden) {
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
    public ResponseEntity<List<RecetaResumenResponse>> buscarPorNombre(@RequestParam String nombre, @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarPorNombre(nombre, orden));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<RecetaResumenResponse>> buscarPorTipo(@PathVariable String tipo, @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarPorTipo(tipo, orden));
    }

    @GetMapping("/ingrediente")
    public ResponseEntity<List<RecetaResumenResponse>> buscarPorIngrediente(@RequestParam String nombreIngrediente, @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarPorIngrediente(nombreIngrediente, orden));
    }

    @GetMapping("/sin-ingrediente")
    public ResponseEntity<List<RecetaResumenResponse>> buscarSinIngrediente(@RequestParam String nombreIngrediente, @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarSinIngrediente(nombreIngrediente, orden));
    }

    @GetMapping("/buscar/usuario")
    public ResponseEntity<List<RecetaResumenResponse>> buscarPorUsuario(
            @RequestParam String nombre,
            @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarPorUsuario(nombre, orden));
    }

    @GetMapping("/ultimas3")
    public List<RecetaDetalleResponse> obtenerUltimas3Recetas() {
        return recetaService.obtenerUltimas3Recetas();
    }

    // Endpoint para buscar recetas por filtros
    @PostMapping("/buscar")
    public ResponseEntity<List<RecetaResumenResponse>> buscarPorFiltros(@RequestBody RecetaFiltroRequest filtro) {
        List<RecetaResumenResponse> resultados = recetaService.buscarPorFiltros(filtro);
        return ResponseEntity.ok(resultados);
    }

    //ENDPOINT PARA EL HOME

    @GetMapping("/ultimas")
    public ResponseEntity<List<RecetaDetalleResponse>> getUltimasRecetas() {
        List<RecetaDetalleResponse> ultimas = recetaService.obtenerUltimasRecetas();
        return ResponseEntity.ok(ultimas);
    }

}