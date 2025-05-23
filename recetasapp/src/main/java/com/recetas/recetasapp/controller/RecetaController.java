package com.recetas.recetasapp.controller;
/* 
import com.recetas.recetasapp.dto.*;
import com.recetas.recetasapp.service.RecetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @GetMapping
    public ResponseEntity<List<RecetaResumenDTO>> listarRecetas(
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long idTipo,
            @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.listarRecetas(idUsuario, idTipo, orden));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaDetalleDTO> obtenerReceta(@PathVariable Long id) {
        return ResponseEntity.ok(recetaService.obtenerRecetaPorId(id));
    }

    @PostMapping
    public ResponseEntity<RecetaDetalleDTO> crearReceta(@RequestBody RecetaCrearDTO dto) {
        return ResponseEntity.status(201).body(recetaService.crearReceta(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecetaDetalleDTO> actualizarReceta(@PathVariable Long id, @RequestBody RecetaCrearDTO dto) {
        return ResponseEntity.ok(recetaService.actualizarReceta(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReceta(@PathVariable Long id) {
        recetaService.eliminarReceta(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecetaResumenDTO>> buscarPorNombre(
            @RequestParam String nombre,
            @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarPorNombre(nombre, orden));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<RecetaResumenDTO>> buscarPorTipo(
            @PathVariable String tipo,
            @RequestParam(required = false) String orden) {
        return ResponseEntity.ok(recetaService.buscarPorTipo(tipo, orden));
    }
}*/
