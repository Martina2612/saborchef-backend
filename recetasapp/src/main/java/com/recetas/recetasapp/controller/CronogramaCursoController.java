package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.CronogramaDTO;
import com.recetas.recetasapp.dto.CursoInscriptoDTO;
import com.recetas.recetasapp.service.CronogramaCursoService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cronogramas")
public class CronogramaCursoController {

    private final CronogramaCursoService cronogramaCursoService;

    public CronogramaCursoController(CronogramaCursoService cronogramaCursoService) {
        this.cronogramaCursoService = cronogramaCursoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CronogramaDTO> obtenerCronogramaPorId(@PathVariable("id") Long id) {
        return cronogramaCursoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/alumno/{idAlumno}/cursos")
public ResponseEntity<List<CursoInscriptoDTO>> getCursosInscriptoPorAlumno(@PathVariable("idAlumno") Long idAlumno) {
    List<CursoInscriptoDTO> lista = cronogramaCursoService.obtenerCursosInscriptoPorAlumno(idAlumno);
    return ResponseEntity.ok(lista);
}



}


