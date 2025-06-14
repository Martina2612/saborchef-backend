package com.recetas.recetasapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;


import com.recetas.recetasapp.dto.CursoDisponibleDTO;
import com.recetas.recetasapp.entity.Curso;
import com.recetas.recetasapp.service.CursoService;
import com.recetas.recetasapp.service.InscripcionCursoService;
import com.recetas.recetasapp.repository.CronogramaCursoRepository;
import com.recetas.recetasapp.service.AsistenciaCursoService;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private InscripcionCursoService inscripcionCursoService;
    
    @Autowired
    private CronogramaCursoRepository cronogramaCursoRepository;

    @Autowired
    private AsistenciaCursoService asistenciaCursoService;

    @GetMapping
    public List<CursoDisponibleDTO> listarCursosDisponibles() {
        return cursoService.listarCursosDisponibles();
    }

    @GetMapping("/{id}")
public ResponseEntity<CursoDisponibleDTO> obtenerCursoPorId(@PathVariable Long id) {
    return cronogramaCursoRepository.findById(id)
        .map(c -> {
            Curso curso = c.getCurso();
            CursoDisponibleDTO dto = new CursoDisponibleDTO(
                curso.getIdCurso(),
                curso.getNombre(),
                curso.getDescripcion(),
                curso.getContenidos(),
                curso.getRequerimientos(),
                curso.getDuracion(),
                curso.getPrecio(),
                curso.getModalidad(),
                curso.getImagenUrl(),
                curso.getNivel().name(), // enum a String
                curso.getChef()
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





    
    
    @PostMapping("/cursos/{idCronograma}/{idAlumno}/inscripcion")
    public ResponseEntity<String> inscribirAlumno(
            @PathVariable Long idCronograma,
            @PathVariable Long idAlumno) {
        try {
            inscripcionCursoService.inscribirAlumno(idCronograma, idAlumno);
            return ResponseEntity.ok("Alumno inscrito con éxito");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error al inscribir: " + e.getMessage());
        }
    }
    
    // Nuevo endpoint para listar alumnos inscritos a un curso
    @GetMapping("/cursos/{id}/alumnos")
    public ResponseEntity<List<String>> listarAlumnosInscritos(@PathVariable Long id) {
        try {
            var cronograma = cronogramaCursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cronograma no encontrado"));
            
            List<String> nombres = cronograma.getAlumnosInscritos().stream()
                .map(alumno -> alumno.getUsuario().getNombre() + " " + alumno.getUsuario().getApellido())
                .toList();
            
            return ResponseEntity.ok(nombres);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
}
}


