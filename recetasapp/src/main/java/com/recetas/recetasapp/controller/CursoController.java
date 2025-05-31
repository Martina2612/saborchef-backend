package com.recetas.recetasapp.controller;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.recetas.recetasapp.dto.CursoDisponibleDTO;
import com.recetas.recetasapp.entity.CronogramaCurso;
import com.recetas.recetasapp.repository.CronogramaCursoRepository;
import com.recetas.recetasapp.service.AsistenciaCursoService;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private CronogramaCursoRepository cronogramaCursoRepository;

    @Autowired
    private AsistenciaCursoService asistenciaCursoService;

    @GetMapping
    public List<CursoDisponibleDTO> listarCursosDisponibles() {
        Date hoy = new Date(System.currentTimeMillis());
        List<CronogramaCurso> cronogramas = cronogramaCursoRepository.findByFechaInicioAfter(hoy);

        return cronogramas.stream()
            .map(c -> new CursoDisponibleDTO(
                c.getIdCronograma(),
                c.getSede().getNombreSede(),
                c.getSede().getDireccionSede(),
                c.getCurso().getDescripcion(),
                c.getCurso().getDuracion(),
                c.getCurso().getPrecio(),
                c.getFechaInicio(),
                c.getFechaFin(),
                c.getVacantesDisponibles()
            ))
            .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
public ResponseEntity<CursoDisponibleDTO> obtenerCursoPorId(@PathVariable Long id) {
    return cronogramaCursoRepository.findById(id)
        .map(c -> {
            CursoDisponibleDTO dto = new CursoDisponibleDTO(
                c.getIdCronograma(),
                c.getSede().getNombreSede(),
                c.getSede().getDireccionSede(),
                c.getCurso().getDescripcion(),
                c.getCurso().getDuracion(),
                c.getCurso().getPrecio(),
                c.getFechaInicio(),
                c.getFechaFin(),
                c.getVacantesDisponibles()
            );
            return ResponseEntity.ok(dto);
        })
        .orElse(ResponseEntity.notFound().build());
}

@PostMapping("/{id}/asistencia")
public ResponseEntity<String> registrarAsistencia(
        @PathVariable("id") Long cursoId,
        @RequestParam("alumnoId") Long alumnoId) {
    String mensaje = asistenciaCursoService.registrarAsistencia(alumnoId, cursoId);
    return ResponseEntity.ok(mensaje);
}


}
