package com.recetas.recetasapp.service.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recetas.recetasapp.dto.ClaseDTO;
import com.recetas.recetasapp.entity.Clase;
import com.recetas.recetasapp.repository.ClaseRepository;
import com.recetas.recetasapp.service.ClaseService;

@Service
public class ClaseServiceImpl implements ClaseService {

    @Autowired
    private ClaseRepository claseRepository;

    @Override
    public List<ClaseDTO> obtenerClasesPorCronograma(Long idCronograma) {
        return claseRepository.findByCronograma_IdCronograma(idCronograma)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private ClaseDTO convertirADTO(Clase clase) {
        ClaseDTO dto = new ClaseDTO();
        dto.setIdClase(clase.getIdClase());
        dto.setTitulo(clase.getTitulo());
        dto.setDescripcion(clase.getDescripcion());
        dto.setNumeroClase(clase.getNumeroClase());
        dto.setFechaClase(clase.getFechaClase());
        return dto;
    }
}

