package com.recetas.recetasapp.service;

import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.dto.AlumnoActualizarDTO;
import com.recetas.recetasapp.dto.ConfirmacionCodigoDTO;
import com.recetas.recetasapp.dto.ResetPasswordDto;
import com.recetas.recetasapp.dto.request.RecoveryRequestDTO;

public interface UsuarioService {
    String recuperarContraseña(String mail);
    Alumno convertirEnAlumno(Long idUsuario, AlumnoActualizarDTO datos);
    String resetearContraseña(ResetPasswordDto datos);
    void confirmarCuentaConCodigo(ConfirmacionCodigoDTO dto);
    Usuario getUserById(Long id);
    Usuario getUserByAlias(String alias);
    boolean verificarCodigo(String email, String codigo);
    void enviarCodigoRecuperacion(RecoveryRequestDTO request);
    String reenviarCodigoConfirmacion(String email);
 
}

