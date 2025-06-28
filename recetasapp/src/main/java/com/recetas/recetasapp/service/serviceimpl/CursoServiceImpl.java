package com.recetas.recetasapp.service.serviceimpl;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recetas.recetasapp.dto.CronogramaDTO;
import com.recetas.recetasapp.dto.CursoDisponibleDTO;
import com.recetas.recetasapp.dto.SedeDTO;
import com.recetas.recetasapp.entity.CronogramaCurso;
import com.recetas.recetasapp.entity.Curso;
import com.recetas.recetasapp.repository.CronogramaCursoRepository;
import com.recetas.recetasapp.service.CursoService;

@Service
public class CursoServiceImpl implements CursoService {

    @Autowired
    private CronogramaCursoRepository cronogramaCursoRepository;
    

    @Override
public List<CursoDisponibleDTO> listarCursosDisponibles() {
    Date hoy = new Date(System.currentTimeMillis());
    List<CronogramaCurso> cronogramas = cronogramaCursoRepository.findByFechaInicioAfter(hoy);

    // Agrupamos cronogramas por curso
    Map<Curso, List<CronogramaCurso>> cursosAgrupados = cronogramas.stream()
        .collect(Collectors.groupingBy(CronogramaCurso::getCurso));

    return cursosAgrupados.entrySet().stream()
        .map(entry -> {
            Curso curso = entry.getKey();
            List<CronogramaDTO> cronogramaDTOs = entry.getValue().stream()
                .map(c -> new CronogramaDTO(
                    c.getIdCronograma(),
                    c.getFechaInicio(),
                    c.getFechaFin(),
                    c.getVacantesDisponibles(),
                    new SedeDTO(
                        c.getSede().getIdSede(),
                        c.getSede().getNombreSede(),
                        c.getSede().getDireccionSede(),
                        c.getSede().getTelefonoSede(),
                        c.getSede().getMailSede(),
                        c.getSede().getWhatsapp(),
                        c.getSede().getTipoBonificacion(),
                        c.getSede().getBonificaCursos(),
                        c.getSede().getTipoPromocion(),
                        c.getSede().getPromocionCursos(),
                        c.getSede().getImagenUrl() // campo nuevo
                    )
                ))
                .collect(Collectors.toList());

            return new CursoDisponibleDTO(
                curso.getIdCurso(),
                curso.getNombre(),
                curso.getDescripcion(),
                curso.getContenidos(),
                curso.getRequerimientos(),
                curso.getDuracion(),
                curso.getPrecio(),
                curso.getModalidad(),
                curso.getImagenUrl(),
                curso.getNivel().name(),
                curso.getChef(),
                cronogramaDTOs
            );
        })
        .collect(Collectors.toList());
}




@Override
public CursoDisponibleDTO obtenerCursoPorId(Long id) {
    List<CronogramaCurso> cronogramas = cronogramaCursoRepository.findByCurso_IdCurso(id);

    if (!cronogramas.isEmpty()) {
        Curso curso = cronogramas.get(0).getCurso(); // Todos los cronogramas pertenecen al mismo curso

        List<CronogramaDTO> cronogramaDTOs = cronogramas.stream()
            .map(c -> new CronogramaDTO(
                c.getIdCronograma(),
                c.getFechaInicio(),
                c.getFechaFin(),
                c.getVacantesDisponibles(),
                new SedeDTO(
                    c.getSede().getIdSede(),
                    c.getSede().getNombreSede(),
                    c.getSede().getDireccionSede(),
                    c.getSede().getTelefonoSede(),
                    c.getSede().getMailSede(),
                    c.getSede().getWhatsapp(),
                    c.getSede().getTipoBonificacion(),
                    c.getSede().getBonificaCursos(),
                    c.getSede().getTipoPromocion(),
                    c.getSede().getPromocionCursos(),
                    c.getSede().getImagenUrl()
                )
            ))
            .collect(Collectors.toList());

        return new CursoDisponibleDTO(
            curso.getIdCurso(),
            curso.getNombre(),
            curso.getDescripcion(),
            curso.getContenidos(),
            curso.getRequerimientos(),
            curso.getDuracion(),
            curso.getPrecio(),
            curso.getModalidad(),
            curso.getImagenUrl(),
            curso.getNivel().name(),
            curso.getChef(),
            cronogramaDTOs
        );
    } else {
        return null;
    }
}





}
