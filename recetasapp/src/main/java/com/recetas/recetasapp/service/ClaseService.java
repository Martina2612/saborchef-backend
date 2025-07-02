package com.recetas.recetasapp.service;

import java.util.List;
import com.recetas.recetasapp.dto.ClaseDTO;

public interface ClaseService {
    List<ClaseDTO> obtenerClasesPorCronograma(Long idCronograma);
}

