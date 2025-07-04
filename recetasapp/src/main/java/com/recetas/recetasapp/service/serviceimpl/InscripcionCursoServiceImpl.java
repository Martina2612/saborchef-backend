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
import com.recetas.recetasapp.service.EmailService;
import com.recetas.recetasapp.service.InscripcionCursoService;

@Service
public class InscripcionCursoServiceImpl implements InscripcionCursoService {

    @Autowired
    private InscripcionCursoRepository inscripcionRepo;

    @Autowired
    private CronogramaCursoRepository cronogramaRepo;

    @Autowired
    private AlumnoRepository alumnoRepo;

    @Autowired
    private EmailService emailService;


    @Override
public InscripcionCurso inscribirAlumno(Long idCronograma, Long idAlumno) {
    CronogramaCurso cronograma = cronogramaRepo.findById(idCronograma)
        .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

    if (cronograma.getVacantesDisponibles() == null || cronograma.getVacantesDisponibles() <= 0) {
        throw new RuntimeException("No hay vacantes disponibles");
    }

    Alumno alumno = alumnoRepo.findById(idAlumno)
        .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

    boolean yaInscripto = inscripcionRepo.existsByAlumnoAndCronograma(alumno, cronograma);
    if (yaInscripto) {
        throw new RuntimeException("El alumno ya está inscripto en este curso");
    }

    // Crear inscripción
    InscripcionCurso inscripcion = new InscripcionCurso();
    inscripcion.setAlumno(alumno);
    inscripcion.setCronograma(cronograma);
    inscripcion.setFechaInscripcion(new Date(System.currentTimeMillis()));
    inscripcionRepo.save(inscripcion);

    // Actualizar vacantes
    cronograma.setVacantesDisponibles(cronograma.getVacantesDisponibles() - 1);
    cronogramaRepo.save(cronograma);

    // Enviar mail de confirmación
    enviarMailConfirmacion(alumno, cronograma);

    return inscripcion;
}

private void enviarMailConfirmacion(Alumno alumno, CronogramaCurso cronograma) {
    String to = alumno.getUsuario().getEmail(); 
    String nombre = alumno.getUsuario().getNombre(); 

    String subject = "Inscripción confirmada: " + cronograma.getCurso().getNombre();
    String body = "Hola " + nombre + ",\n\n" +
            "Te confirmamos que te inscribiste correctamente al curso: " + cronograma.getCurso().getNombre() + ".\n" +
            "Fecha de inicio: " + cronograma.getFechaInicio() + "\n" +
            "Gracias por confiar en SaborChef.\n\n" +
            "Este mail es tu comprobante.";

    emailService.enviarEmail(to, subject, body); 
}

public void darDeBaja(Long idCronograma, Long idAlumno) {
    // Buscar la inscripción
    InscripcionCurso inscripcion = inscripcionRepo
        .findByAlumno_IdAlumnoAndCronograma_IdCronograma(idAlumno, idCronograma)
        .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
    
    // Obtener el cronograma para actualizar las vacantes
    CronogramaCurso cronograma = inscripcion.getCronograma();
    
    // Incrementar las vacantes disponibles
    cronograma.setVacantesDisponibles(cronograma.getVacantesDisponibles() + 1);
    
    // Eliminar la inscripción
    inscripcionRepo.delete(inscripcion);
    
    
    cronogramaRepo.save(cronograma);
}


}
