package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.ClaseDTO;
import com.recetas.recetasapp.service.AsistenciaCursoService;
import com.recetas.recetasapp.service.ClaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    @Autowired
    private ClaseService claseService;

    @Autowired
    private AsistenciaCursoService asistenciaCursoService;

    @GetMapping("/cronograma/{idCronograma}")
    public List<ClaseDTO> obtenerClasesPorCronograma(@PathVariable("idCronograma") Long idCronograma) {
        return claseService.obtenerClasesPorCronograma(idCronograma);
    }

    @PostMapping("/{claseId}/asistencia")
    public ResponseEntity<String> registrarAsistencia(
            @PathVariable("claseId") Long claseId,
            @RequestParam("alumnoId") Long alumnoId) {
        String mensaje = asistenciaCursoService.registrarAsistencia(alumnoId, claseId);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/{claseId}/asistencia")
    public ResponseEntity<Boolean> verificarAsistencia(
            @PathVariable("claseId") Long claseId,
            @RequestParam("alumnoId") Long alumnoId) {
        boolean asistio = asistenciaCursoService.asistioAClase(alumnoId, claseId);
        return ResponseEntity.ok(asistio);
    }
}
