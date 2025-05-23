package com.recetas.recetasapp.service;

import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.dto.AlumnoActualizarDTO;

public interface UsuarioService {
    Usuario iniciarRegistro(String mail, String alias);
    boolean confirmarRegistro(String mail, String codigo, String password, String nombre, String apellido);
    Usuario login(String mail, String password);
    Alumno convertirEnAlumno(Long idUsuario, AlumnoActualizarDTO datos);
}

