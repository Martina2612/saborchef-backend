package com.recetas.recetasapp.service.serviceimpl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recetas.recetasapp.dto.CronogramaDTO;
import com.recetas.recetasapp.dto.CursoInscriptoDTO;
import com.recetas.recetasapp.dto.SedeDTO;
import com.recetas.recetasapp.entity.CronogramaCurso;
import com.recetas.recetasapp.entity.Sede;
import com.recetas.recetasapp.repository.AsistenciaCursoRepository;
import com.recetas.recetasapp.repository.CronogramaCursoRepository;
import com.recetas.recetasapp.repository.InscripcionCursoRepository;
import com.recetas.recetasapp.service.CronogramaCursoService;
import com.recetas.recetasapp.entity.Curso;


@Service
public class CronogramaCursoServiceImpl implements CronogramaCursoService {

    
    private final InscripcionCursoRepository inscripcionCursoRepository;

    private final CronogramaCursoRepository cronogramaCursoRepository;

    private final AsistenciaCursoRepository asistenciaRepo;

    public CronogramaCursoServiceImpl(CronogramaCursoRepository cronogramaRepo, InscripcionCursoRepository inscripcionRepo,AsistenciaCursoRepository asistenciaRepo) {
        this.cronogramaCursoRepository = cronogramaRepo;
        this.inscripcionCursoRepository = inscripcionRepo;
        this.asistenciaRepo = asistenciaRepo;
    }
    

    @Override
    public Optional<CronogramaDTO> obtenerPorId(Long id) {
        return cronogramaCursoRepository.findById(id)
                .map(this::convertirADTO);
    }

    private CronogramaDTO convertirADTO(CronogramaCurso cronograma) {
        Sede sede = cronograma.getSede();
        SedeDTO sedeDTO = new SedeDTO(
    sede.getIdSede(),
    sede.getNombreSede(),
    sede.getDireccionSede(),
    sede.getTelefonoSede(),
    sede.getMailSede(),
    sede.getWhatsapp(),
    sede.getTipoBonificacion(),
    sede.getBonificaCursos(),
    sede.getTipoPromocion(),
    sede.getPromocionCursos(),
    sede.getImagenUrl()
);


        return new CronogramaDTO(
            cronograma.getIdCronograma(),
            cronograma.getFechaInicio(),
            cronograma.getFechaFin(),
            cronograma.getVacantesDisponibles(),
            sedeDTO
        );
    }

    public List<CursoInscriptoDTO> obtenerCursosInscriptoPorAlumno(Long idAlumno) {
        return inscripcionCursoRepository.findByAlumno_IdAlumno(idAlumno)
            .stream()
            .map(inscripcion -> {
                CronogramaCurso cronograma = inscripcion.getCronograma();
                Curso curso = cronograma.getCurso();
                Sede sede = cronograma.getSede();
            
                long totalDias = diasEntre(cronograma.getFechaInicio(), cronograma.getFechaFin());
                int asistencias = asistenciaRepo.countByAlumno_IdAlumnoAndCronograma_IdCronograma(
                    inscripcion.getAlumno().getIdAlumno(),
                    cronograma.getIdCronograma()
                );
            
                boolean finalizado = !cronograma.getFechaFin().toLocalDate().isAfter(LocalDate.now());
                float progreso = finalizado ? 100f : (totalDias == 0 ? 0f : (float) asistencias / totalDias * 100);
            
                return new CursoInscriptoDTO(
                    curso.getIdCurso(),
                    curso.getNombre(),
                    curso.getDescripcion(),
                    curso.getModalidad(),
                    curso.getImagenUrl(),
                    curso.getNivel().name(),
                    curso.getPrecio(),
                    curso.getDuracion(),      
                    curso.getChef(),          
                    cronograma.getFechaInicio(),
                    cronograma.getFechaFin(),
                    new SedeDTO(
                        sede.getIdSede(),
                        sede.getNombreSede(),
                        sede.getDireccionSede(),
                        sede.getTelefonoSede(),
                        sede.getMailSede(),
                        sede.getWhatsapp(),
                        sede.getTipoBonificacion(),
                        sede.getBonificaCursos(),
                        sede.getTipoPromocion(),
                        sede.getPromocionCursos(),
                        sede.getImagenUrl()
                    ),
                    progreso,
                    finalizado
                );

            })
            
            .toList();
    }

    private long diasEntre(Date inicio, Date fin) {
        return (fin.toLocalDate().toEpochDay() - inicio.toLocalDate().toEpochDay()) + 1;
    }
    
    


}



