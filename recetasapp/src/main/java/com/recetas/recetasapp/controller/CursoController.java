package com.recetas.recetasapp.controller;

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

import org.springframework.web.bind.annotation.*;

import com.recetas.recetasapp.dto.CronogramaDTO;
import com.recetas.recetasapp.dto.CursoDisponibleDTO;
import com.recetas.recetasapp.dto.SedeDTO;
import com.recetas.recetasapp.entity.CronogramaCurso;
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
public List<CursoDisponibleDTO> listarCursosDisponibles(@RequestParam("idUsuario") Long idUsuario) {
    return cursoService.listarCursosDisponibles(idUsuario);
}


    @GetMapping("/{id}")
public ResponseEntity<CursoDisponibleDTO> obtenerCursoPorId(@PathVariable("id") Long id) {
    CursoDisponibleDTO dto = cursoService.obtenerCursoPorId(id);
    if (dto == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(dto);
}




@PostMapping("/clase/{claseId}/asistencia")
public ResponseEntity<String> registrarAsistencia(
        @PathVariable("claseId") Long claseId,
        @RequestParam("alumnoId") Long alumnoId) {
    String mensaje = asistenciaCursoService.registrarAsistencia(alumnoId, claseId);
    return ResponseEntity.ok(mensaje);
}






    
    
@PostMapping("/{idCronograma}/{idAlumno}/inscripcion")
public ResponseEntity<String> inscribirAlumno(
        @PathVariable("idCronograma") Long idCronograma,
        @PathVariable("idAlumno") Long idAlumno) {
    try {
        inscripcionCursoService.inscribirAlumno(idCronograma, idAlumno);
        return ResponseEntity.ok("Alumno inscrito con éxito");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body("Error al inscribir: " + e.getMessage());
    }
}

    
    // Nuevo endpoint para listar alumnos inscritos a un curso
    @GetMapping("/{id}/alumnos")
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

@DeleteMapping("/{idCronograma}/{idAlumno}/baja")
public ResponseEntity<String> darDeBaja(
        @PathVariable("idCronograma") Long idCronograma,
        @PathVariable("idAlumno") Long idAlumno) {
    try {
        inscripcionCursoService.darDeBaja(idCronograma, idAlumno);
        return ResponseEntity.ok("Alumno dado de baja con éxito");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body("Error al dar de baja: " + e.getMessage());
    }
}

}


