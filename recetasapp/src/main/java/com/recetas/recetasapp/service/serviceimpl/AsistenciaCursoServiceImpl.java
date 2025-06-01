package com.recetas.recetasapp.service.serviceimpl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.entity.AsistenciaCurso;
import com.recetas.recetasapp.entity.CronogramaCurso;
import com.recetas.recetasapp.entity.InscripcionCurso;
import com.recetas.recetasapp.repository.AlumnoRepository;
import com.recetas.recetasapp.repository.AsistenciaCursoRepository;
import com.recetas.recetasapp.repository.CronogramaCursoRepository;
import com.recetas.recetasapp.repository.InscripcionCursoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsistenciaCursoServiceImpl extends AsistenciaCurso {

    private final AsistenciaCursoRepository asistenciaRepo;
    private final AlumnoRepository alumnoRepo;
    private final CronogramaCursoRepository cronogramaRepo;
    private final InscripcionCursoRepository inscripcionRepo;

    @Transactional
    public String registrarAsistencia(Long alumnoId, Long cursoId) {
        Date hoy = Date.valueOf(LocalDate.now());

        Alumno alumno = alumnoRepo.findById(alumnoId)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        // Buscar todos los cronogramas de ese curso
        List<CronogramaCurso> cronogramas = cronogramaRepo.findByCursoId(cursoId);

        // Buscar un cronograma activo hoy
        CronogramaCurso cronogramaHoy = cronogramas.stream()
            .filter(c -> !hoy.before(c.getFechaInicio()) && !hoy.after(c.getFechaFin()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No hay clase hoy para este curso."));

        // Verificar que el alumno esté inscripto en ese cronograma
        InscripcionCurso inscripcion = inscripcionRepo
            .findByAlumnoIdAndCronogramaId(alumnoId, cronogramaHoy.getIdCronograma())
            .orElseThrow(() -> new RuntimeException("El alumno no está inscripto en este cronograma."));

        // Verificar si ya registró asistencia hoy
        boolean yaAsistio = asistenciaRepo.existsByAlumnoAndCronogramaAndFecha(alumno, cronogramaHoy, hoy);
        if (yaAsistio) {
            throw new RuntimeException("Ya se registró la asistencia para hoy.");
        }

        AsistenciaCurso asistencia = new AsistenciaCurso();
        asistencia.setAlumno(alumno);
        asistencia.setCronograma(cronogramaHoy);
        asistencia.setFecha(hoy);

        asistenciaRepo.save(asistencia);

        return "Asistencia registrada correctamente.";
    }
}

