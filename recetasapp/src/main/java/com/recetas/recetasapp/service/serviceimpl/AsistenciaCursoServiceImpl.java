package com.recetas.recetasapp.service.serviceimpl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.entity.AsistenciaCurso;
import com.recetas.recetasapp.entity.Clase;
import com.recetas.recetasapp.entity.CronogramaCurso;
import com.recetas.recetasapp.entity.InscripcionCurso;
import com.recetas.recetasapp.repository.AlumnoRepository;
import com.recetas.recetasapp.repository.AsistenciaCursoRepository;
import com.recetas.recetasapp.repository.ClaseRepository;
import com.recetas.recetasapp.repository.CronogramaCursoRepository;
import com.recetas.recetasapp.repository.InscripcionCursoRepository;
import com.recetas.recetasapp.service.AsistenciaCursoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class AsistenciaCursoServiceImpl implements AsistenciaCursoService {

    private final AsistenciaCursoRepository asistenciaRepo;
    private final AlumnoRepository alumnoRepo;
    private final CronogramaCursoRepository cronogramaRepo;
    private final InscripcionCursoRepository inscripcionRepo;
    private final ClaseRepository claseRepo;

    @Transactional
public String registrarAsistencia(Long alumnoId, Long claseId) {
    // Obtener la clase y su fecha
    Clase clase = claseRepo.findById(claseId)
        .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
    Date fechaClase = clase.getFechaClase();

    // Verificar que sea hoy
    Date hoy = Date.valueOf(LocalDate.now());
    if (!fechaClase.equals(hoy)) {
        throw new RuntimeException("La asistencia solo puede registrarse el día de la clase.");
    }

    // Obtener alumno
    Alumno alumno = alumnoRepo.findById(alumnoId)
        .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

    // Verificar si ya asistió
    boolean yaAsistio = asistenciaRepo.existsByAlumnoAndClase(alumno, clase);
    if (yaAsistio) {
        throw new RuntimeException("Ya se registró la asistencia para esta clase.");
    }

    // Registrar asistencia
    AsistenciaCurso asistencia = new AsistenciaCurso();
    asistencia.setAlumno(alumno);
    asistencia.setClase(clase); 
    asistencia.setFecha(hoy);

    asistenciaRepo.save(asistencia);

    return "Asistencia registrada correctamente.";
}

public boolean asistioAClase(Long alumnoId, Long claseId) {
    Alumno alumno = alumnoRepo.findById(alumnoId)
        .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
    Clase clase = claseRepo.findById(claseId)
        .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

    return asistenciaRepo.existsByAlumnoAndClase(alumno, clase);
}



}

