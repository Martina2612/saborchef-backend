package com.recetas.recetasapp.service;

import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.dto.AlumnoActualizarDTO;
import com.recetas.recetasapp.dto.ResetPasswordDto;

public interface UsuarioService {
    String recuperarContraseña(String mail);
    Alumno convertirEnAlumno(Long idUsuario, AlumnoActualizarDTO datos);
    String resetearContraseña(ResetPasswordDto datos);

}

