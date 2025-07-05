package com.recetas.recetasapp.service.serviceimpl;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recetas.recetasapp.dto.BajaCursoResponse;
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
    String to = alumno.getUsuario().getEmail(); // ✅ accediendo desde Usuario
    String nombre = alumno.getUsuario().getNombre(); // ✅ accediendo desde Usuario

    String subject = "Inscripción confirmada: " + cronograma.getCurso().getNombre();
    String body = "Hola " + nombre + ",\n\n" +
            "Te confirmamos que te inscribiste correctamente al curso: " + cronograma.getCurso().getNombre() + ".\n" +
            "Fecha de inicio: " + cronograma.getFechaInicio() + "\n" +
            "Gracias por confiar en SaborChef.\n\n" +
            "Este mail es tu comprobante.";

    emailService.enviarEmail(to, subject, body); // ✅ usás tu EmailService
}

public BajaCursoResponse darDeBaja(Long idCronograma, Long idAlumno) {
    InscripcionCurso inscripcion = inscripcionRepo
        .findByAlumno_IdAlumnoAndCronograma_IdCronograma(idAlumno, idCronograma)
        .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
    
    // Obtener datos necesarios
    Date fechaInscripcion = inscripcion.getFechaInscripcion();
    Date fechaInicio = inscripcion.getCronograma().getFechaInicio();
    Double precioCurso = inscripcion.getCronograma().getCurso().getPrecio();
    
    // Calcular reintegro
    BajaCursoResponse reintegroInfo = calcularReintegro(fechaInicio, precioCurso);
    
    // Eliminar inscripción
    inscripcionRepo.delete(inscripcion);
    
    return reintegroInfo;
}

public BajaCursoResponse calcularReintegro(Date fechaInicio, Double precioCurso) {
    LocalDate hoy = LocalDate.now();
    LocalDate inicioDate = fechaInicio.toLocalDate();
    
    // Calcular días hábiles hasta el inicio (excluyendo sábados y domingos)
    long diasHabiles = contarDiasHabiles(hoy, inicioDate);
    
    String mensaje;
    Double porcentajeReintegro;
    Double montoReintegro;
    String tipoReintegro;
    
    if (hoy.isAfter(inicioDate)) {
        // Curso ya iniciado
        mensaje = "El curso ya ha iniciado. No se realizará reintegro alguno.";
        porcentajeReintegro = 0.0;
        montoReintegro = 0.0;
        tipoReintegro = "SIN_REINTEGRO";
    } else if (hoy.equals(inicioDate)) {
        // Día de inicio
        mensaje = "Se reintegrará el 50% del valor del curso ($" + String.format("%.2f", precioCurso * 0.5) + ").";
        porcentajeReintegro = 50.0;
        montoReintegro = precioCurso * 0.5;
        tipoReintegro = "PARCIAL_50";
    } else if (diasHabiles < 10) {
        // Entre 1-9 días hábiles antes
        mensaje = "Se reintegrará el 70% del valor del curso ($" + String.format("%.2f", precioCurso * 0.7) + ").";
        porcentajeReintegro = 70.0;
        montoReintegro = precioCurso * 0.7;
        tipoReintegro = "PARCIAL_70";
    } else {
        // 10 o más días hábiles antes
        mensaje = "Se reintegrará el 100% del valor del curso ($" + String.format("%.2f", precioCurso) + ").";
        porcentajeReintegro = 100.0;
        montoReintegro = precioCurso;
        tipoReintegro = "COMPLETO";
    }
    
    return new BajaCursoResponse(mensaje, montoReintegro, porcentajeReintegro, precioCurso, tipoReintegro);
}

private long contarDiasHabiles(LocalDate desde, LocalDate hasta) {
    long diasHabiles = 0;
    LocalDate fecha = desde;
    
    while (fecha.isBefore(hasta)) {
        if (fecha.getDayOfWeek() != DayOfWeek.SATURDAY && 
            fecha.getDayOfWeek() != DayOfWeek.SUNDAY) {
            diasHabiles++;
        }
        fecha = fecha.plusDays(1);
    }
    
    return diasHabiles;
}

public BajaCursoResponse calcularReintegroSinEjecutar(Long idCronograma, Long idAlumno) {
    InscripcionCurso inscripcion = inscripcionRepo
        .findByAlumno_IdAlumnoAndCronograma_IdCronograma(idAlumno, idCronograma)
        .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
    
    Date fechaInicio = inscripcion.getCronograma().getFechaInicio();
    Double precioCurso = inscripcion.getCronograma().getCurso().getPrecio();
    
    return calcularReintegro(fechaInicio, precioCurso);
}
}



