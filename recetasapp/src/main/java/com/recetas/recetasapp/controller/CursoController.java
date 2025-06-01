package com.recetas.recetasapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.recetas.recetasapp.dto.CursoDisponibleDTO;
import com.recetas.recetasapp.service.CursoService;
import com.recetas.recetasapp.service.InscripcionCursoService;
import com.recetas.recetasapp.repository.CronogramaCursoRepository;

@RestController
@RequestMapping("/api")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private InscripcionCursoService inscripcionCursoService;
    
    @Autowired
    private CronogramaCursoRepository cronogramaCursoRepository;

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