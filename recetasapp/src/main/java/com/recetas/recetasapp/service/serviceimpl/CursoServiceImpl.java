package com.recetas.recetasapp.service.serviceimpl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recetas.recetasapp.dto.CursoDisponibleDTO;
import com.recetas.recetasapp.entity.CronogramaCurso;
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

        return cronogramas.stream()
            .map(c -> new CursoDisponibleDTO(
                c.getIdCronograma(),
                c.getSede().getNombreSede(),
                c.getSede().getDireccionSede(),
                c.getCurso().getDescripcion(),
                c.getCurso().getDuracion(),
                c.getCurso().getPrecio(),
                c.getFechaInicio(),
                c.getFechaFin(),
                c.getVacantesDisponibles()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public CursoDisponibleDTO obtenerCursoPorId(Long id) {
        Optional<CronogramaCurso> optional = cronogramaCursoRepository.findById(id);

        if (optional.isPresent()) {
            CronogramaCurso c = optional.get();
            return new CursoDisponibleDTO(
                c.getIdCronograma(),
                c.getSede().getNombreSede(),
                c.getSede().getDireccionSede(),
                c.getCurso().getDescripcion(),
                c.getCurso().getDuracion(),
                c.getCurso().getPrecio(),
                c.getFechaInicio(),
                c.getFechaFin(),
                c.getVacantesDisponibles()
            );
        } else {
            return null;
        }
    }
}
