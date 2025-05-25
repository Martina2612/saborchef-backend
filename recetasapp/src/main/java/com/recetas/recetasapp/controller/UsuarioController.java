package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.AlumnoActualizarDTO;
import com.recetas.recetasapp.dto.ResetPasswordDto;
import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para recuperar la contraseña
    @GetMapping("/recuperar")
    public ResponseEntity<String> recuperarContraseña(@RequestParam("email") String email) {
        String mensaje = usuarioService.recuperarContraseña(email);
        return ResponseEntity.ok(mensaje);
}


    // Endpoint para convertir un usuario en alumno
    @PostMapping("/{id}/convertir-alumno")
    public Alumno convertirEnAlumno(@PathVariable("id") Long id, @RequestBody AlumnoActualizarDTO datos) {
        return usuarioService.convertirEnAlumno(id, datos);
}

 @PostMapping("/password/reset")
public String resetearContraseña(@RequestBody ResetPasswordDto datos) {
    return usuarioService.resetearContraseña(datos);
}

}

