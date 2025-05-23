package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.entity.Sede;
import com.recetas.recetasapp.service.SedeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    private final SedeService sedeService;

    public SedeController(SedeService sedeService) {
        this.sedeService = sedeService;
    }

    // Guardar nueva sede
    @PostMapping
    public ResponseEntity<Sede> crearSede(@RequestBody Sede sede) {
        Sede sedeGuardada = sedeService.guardarSede(sede);
        return new ResponseEntity<>(sedeGuardada, HttpStatus.CREATED);
    }

    // Obtener todas las sedes
    @GetMapping
    public ResponseEntity<List<Sede>> listarSedes() {
        List<Sede> sedes = sedeService.obtenerTodasLasSedes();
        return ResponseEntity.ok(sedes);
    }

    // Obtener sede por id
    @GetMapping("/{id}")
    public ResponseEntity<Sede> obtenerSedePorId(@PathVariable Long id) {
        return sedeService.obtenerSedePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
