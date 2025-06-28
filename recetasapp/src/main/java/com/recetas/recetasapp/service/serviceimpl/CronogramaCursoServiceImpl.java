package com.recetas.recetasapp.service.serviceimpl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.recetas.recetasapp.dto.CronogramaDTO;
import com.recetas.recetasapp.dto.SedeDTO;
import com.recetas.recetasapp.entity.CronogramaCurso;
import com.recetas.recetasapp.entity.Sede;
import com.recetas.recetasapp.repository.CronogramaCursoRepository;
import com.recetas.recetasapp.service.CronogramaCursoService;

@Service
public class CronogramaCursoServiceImpl implements CronogramaCursoService {

    private final CronogramaCursoRepository cronogramaCursoRepository;

    public CronogramaCursoServiceImpl(CronogramaCursoRepository repo) {
        this.cronogramaCursoRepository = repo;
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
}



