package com.recetas.recetasapp.service.serviceimpl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recetas.recetasapp.dto.CursoDisponibleDTO;
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

        return cronogramas.stream()
    .map(c -> {
        Curso curso = c.getCurso();
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
            curso.getNivel().name(), // enum a String
            curso.getChef()
        );
    })
    .collect(Collectors.toList());
    }

    @Override
public CursoDisponibleDTO obtenerCursoPorId(Long id) {
    Optional<CronogramaCurso> optional = cronogramaCursoRepository.findById(id);

    if (optional.isPresent()) {
        CronogramaCurso cronograma = optional.get();
        Curso curso = cronograma.getCurso();

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
            curso.getNivel().name(), // convert enum to String
            curso.getChef()
        );
    } else {
        return null;
    }
}

}
