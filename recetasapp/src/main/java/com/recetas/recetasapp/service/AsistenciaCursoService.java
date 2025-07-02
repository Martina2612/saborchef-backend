package com.recetas.recetasapp.service;

public interface AsistenciaCursoService {
    String registrarAsistencia(Long alumnoId, Long claseId);
    public boolean asistioAClase(Long alumnoId, Long claseId);
}
