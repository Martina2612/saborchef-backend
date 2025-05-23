package com.recetas.recetasapp.service;

import com.recetas.recetasapp.entity.Sede;

import java.util.List;
import java.util.Optional;

public interface SedeService {
    Sede guardarSede(Sede sede);
    List<Sede> obtenerTodasLasSedes();
    Optional<Sede> obtenerSedePorId(Long id);
}
