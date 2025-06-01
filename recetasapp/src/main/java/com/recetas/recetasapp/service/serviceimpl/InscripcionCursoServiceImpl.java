package com.recetas.recetasapp.service.serviceimpl;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.entity.CronogramaCurso;
import com.recetas.recetasapp.entity.InscripcionCurso;
import com.recetas.recetasapp.repository.AlumnoRepository;
import com.recetas.recetasapp.repository.CronogramaCursoRepository;
import com.recetas.recetasapp.repository.InscripcionCursoRepository;
import com.recetas.recetasapp.service.InscripcionCursoService;

@Service
public class InscripcionCursoServiceImpl implements InscripcionCursoService {

    @Autowired
    private InscripcionCursoRepository inscripcionRepo;

    @Autowired
    private CronogramaCursoRepository cronogramaRepo;

    @Autowired
    private AlumnoRepository alumnoRepo;

    @Override
    public InscripcionCurso inscribirAlumno(Long idCronograma, Long idAlumno) {
        CronogramaCurso cronograma = cronogramaRepo.findById(idCronograma)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        if (cronograma.getVacantesDisponibles() == null || cronograma.getVacantesDisponibles() <= 0) {
            throw new RuntimeException("No hay vacantes disponibles");
        }

        Alumno alumno = alumnoRepo.findById(idAlumno)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        // Verificar si ya está inscripto
        boolean yaInscripto = inscripcionRepo.existsByAlumnoAndCronograma(alumno, cronograma);
        if (yaInscripto) {
            throw new RuntimeException("El alumno ya está inscripto en este curso");
        }

        // Crear y guardar inscripción
        InscripcionCurso inscripcion = new InscripcionCurso();
        inscripcion.setAlumno(alumno);
        inscripcion.setCronograma(cronograma);
        inscripcion.setFechaInscripcion(new Date(System.currentTimeMillis()));
        inscripcionRepo.save(inscripcion);

        // Restar una vacante
        cronograma.setVacantesDisponibles(cronograma.getVacantesDisponibles() - 1);
        cronogramaRepo.save(cronograma);

        return inscripcion;
    }
}
