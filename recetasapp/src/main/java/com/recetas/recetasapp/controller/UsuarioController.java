package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.AlumnoActualizarDTO;
import com.recetas.recetasapp.dto.CodigoVerificacionDto;
import com.recetas.recetasapp.dto.ConfirmacionCodigoDTO;
import com.recetas.recetasapp.dto.EmailDTO;
import com.recetas.recetasapp.dto.ResetPasswordDto;
import com.recetas.recetasapp.dto.request.RecoveryRequestDTO;
import com.recetas.recetasapp.dto.response.PasswordResetResponse;
import com.recetas.recetasapp.dto.response.VerifyCodeResponse;
import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto datos) {
    usuarioService.resetearContraseña(datos);
    return ResponseEntity.ok(new PasswordResetResponse("Contraseña actualizada correctamente", true));
}



@PostMapping("/confirmar-codigo")
public ResponseEntity<String> confirmarCuenta(@RequestBody ConfirmacionCodigoDTO dto) {
    usuarioService.confirmarCuentaConCodigo(dto);
    return ResponseEntity.ok("Cuenta confirmada con éxito.");
}


@GetMapping("/{id}")
public Usuario getUsuarioById(@PathVariable(name = "id") Long id) {
    return usuarioService.getUserById(id);
}

@PostMapping("/password/send-code")
    public ResponseEntity<PasswordResetResponse> enviarCodigo(
            @RequestBody EmailDTO dto) {
        RecoveryRequestDTO request = new RecoveryRequestDTO(dto.getEmail());
        usuarioService.enviarCodigoRecuperacion(request);

        return ResponseEntity.ok(
                new PasswordResetResponse("Código enviado correctamente", true)
        );
    }


@PostMapping("/password/verify-code")
public ResponseEntity<VerifyCodeResponse> verificarCodigo(@RequestBody CodigoVerificacionDto dto) {
    
    
    
    try {
        boolean valido = usuarioService.verificarCodigo(dto.getEmail(), dto.getCodigo());
        System.out.println("✅ Resultado validación: " + valido);
        
        if (valido) {
            System.out.println("🎉 Código VÁLIDO - devolviendo success");
            return ResponseEntity.ok(new VerifyCodeResponse(true, "Código correcto"));
        } else {
            System.out.println("❌ Código INVÁLIDO - devolviendo error 400");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new VerifyCodeResponse(false, "El código ingresado es incorrecto o expiró"));
        }
        
    } catch (Exception e) {
        System.out.println("💥 Excepción en verificarCodigo: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new VerifyCodeResponse(false, "Error interno del servidor"));
    }
}

@PostMapping("/codigo/reenviar")
public ResponseEntity<String> reenviarCodigo(@RequestBody Map<String, String> body) {
    String email = body.get("email");
    String respuesta = usuarioService.reenviarCodigoConfirmacion(email);
    return ResponseEntity.ok(respuesta);
}








}

