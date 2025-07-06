package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.AlumnoActualizarDTO;
import com.recetas.recetasapp.dto.CodigoVerificacionDto;
import com.recetas.recetasapp.dto.ConfirmacionCodigoDTO;
import com.recetas.recetasapp.dto.EmailDTO;
import com.recetas.recetasapp.dto.ResetPasswordDto;
import com.recetas.recetasapp.dto.UsuarioPerfilDTO; // NUEVO IMPORT
import com.recetas.recetasapp.dto.request.RecoveryRequestDTO;
import com.recetas.recetasapp.dto.response.PasswordResetResponse;
import com.recetas.recetasapp.dto.response.VerifyCodeResponse;
import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // NUEVO IMPORT

import java.io.IOException; // NUEVO IMPORT
import java.nio.file.Files; // NUEVO IMPORT
import java.nio.file.Path; // NUEVO IMPORT
import java.nio.file.Paths; // NUEVO IMPORT
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

    // Endpoint para resetear contraseña
    @PostMapping("/password/reset")
    public ResponseEntity<PasswordResetResponse> resetPassword(@RequestBody ResetPasswordDto datos) {
        usuarioService.resetearContraseña(datos);
        return ResponseEntity.ok(new PasswordResetResponse("Contraseña actualizada correctamente", true));
    }

    // Endpoint para confirmar cuenta con código
    @PostMapping("/confirmar-codigo")
    public ResponseEntity<String> confirmarCuenta(@RequestBody ConfirmacionCodigoDTO dto) {
        usuarioService.confirmarCuentaConCodigo(dto);
        return ResponseEntity.ok("Cuenta confirmada con éxito.");
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.getUserById(id);
        return ResponseEntity.ok(usuario);
    }

    // Enviar código de recuperación de contraseña
    @PostMapping("/password/send-code")
    public ResponseEntity<PasswordResetResponse> enviarCodigo(@RequestBody EmailDTO dto) {
        RecoveryRequestDTO request = new RecoveryRequestDTO(dto.getEmail());
        usuarioService.enviarCodigoRecuperacion(request);
        return ResponseEntity.ok(new PasswordResetResponse("Código enviado correctamente", true));
    }

    // Verificar código de recuperación
    @PostMapping("/password/verify-code")
    public ResponseEntity<VerifyCodeResponse> verificarCodigo(@RequestBody CodigoVerificacionDto dto) {
        boolean valido = usuarioService.verificarCodigo(dto.getEmail(), dto.getCodigo());
        if (valido) {
            return ResponseEntity.ok(new VerifyCodeResponse(true, "Código correcto"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new VerifyCodeResponse(false, "El código ingresado es incorrecto o expiró"));
        }
    }

    // Reenviar código de confirmación
    @PostMapping("/codigo/reenviar")
    public ResponseEntity<String> reenviarCodigo(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String respuesta = usuarioService.reenviarCodigoConfirmacion(email);
        return ResponseEntity.ok(respuesta);
    }

    // ------------------------------------------------------------------------------------
    // Endpoints para validar alias/email

    /**
     * Comprueba si un alias ya existe.
     * Devuelve { "exists": true/false }
     */
    @GetMapping("/alias-exists/{alias}")
    public ResponseEntity<Map<String, Boolean>> aliasExists(@PathVariable("alias") String alias) {
        boolean exists = usuarioService.aliasExists(alias);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Comprueba si un email ya está registrado.
     */
    @GetMapping("/email-exists")
    public ResponseEntity<Map<String, Boolean>> emailExists(@RequestParam("email") String email) {
        boolean exists = usuarioService.emailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // ------------------------------------------------------------------------------------
    // NUEVOS ENDPOINTS PARA PERFIL DE USUARIO

    /**
     * Obtiene el perfil de un usuario por ID
     * GET /api/usuarios/perfil/{userId}
     */
    @GetMapping("/perfil/{userId}")
    public ResponseEntity<UsuarioPerfilDTO> obtenerPerfil(@PathVariable Long userId) {
        try {
            UsuarioPerfilDTO perfil = usuarioService.obtenerPerfil(userId);
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza el perfil de un usuario
     * PUT /api/usuarios/perfil/{userId}
     */
    @PutMapping("/perfil/{userId}")
    public ResponseEntity<UsuarioPerfilDTO> actualizarPerfil(
            @PathVariable Long userId,
            @RequestBody UsuarioPerfilDTO perfilDTO) {
        try {
            UsuarioPerfilDTO perfilActualizado = usuarioService.actualizarPerfil(userId, perfilDTO);
            return ResponseEntity.ok(perfilActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Sube una foto de perfil para un usuario
     * POST /api/usuarios/perfil/{userId}/foto
     */
    @PostMapping("/perfil/{userId}/foto")
    public ResponseEntity<Map<String, String>> subirFotoPerfil(
            @PathVariable Long userId,
            @RequestParam("foto") MultipartFile file) {
        try {
            // Validar archivo
            if (file.isEmpty() || file.getSize() > 5 * 1024 * 1024) { // 5MB max
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Archivo inválido o muy grande"));
            }
            
            // Crear directorio si no existe
            String uploadDir = "uploads/profile_photos/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generar nombre único
            String fileName = "user_" + userId + "_" + System.currentTimeMillis() + ".jpg";
            Path filePath = uploadPath.resolve(fileName);
            
            // Guardar archivo
            Files.copy(file.getInputStream(), filePath);
            
            // Actualizar URL en base de datos
            String fotoUrl = "/api/imagenes/perfil/" + fileName;
            usuarioService.actualizarFotoPerfil(userId, fotoUrl);
            
            return ResponseEntity.ok(Map.of("fotoUrl", fotoUrl));
            
        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al guardar la imagen"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error en la solicitud"));
        }
    }
}