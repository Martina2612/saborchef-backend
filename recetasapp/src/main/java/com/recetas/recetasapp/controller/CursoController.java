package com.recetas.recetasapp.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.recetas.recetasapp.dto.CursoDisponibleDTO;
import com.recetas.recetasapp.service.CursoService;

@RestController
@RequestMapping("/api")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping("/cursos")
    public List<CursoDisponibleDTO> listarCursosDisponibles() {
        return cursoService.listarCursosDisponibles();
    }

    @GetMapping("/cursos/{id}")
    public ResponseEntity<CursoDisponibleDTO> obtenerCursoPorId(@PathVariable Long id) {
        CursoDisponibleDTO dto = cursoService.obtenerCursoPorId(id);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
