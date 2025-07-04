package com.recetas.recetasapp.service.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.exception.auth.UserNotFoundException;
import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.entity.Rol;
import com.recetas.recetasapp.dto.AlumnoActualizarDTO;
import com.recetas.recetasapp.dto.ConfirmacionCodigoDTO;
import com.recetas.recetasapp.dto.request.RecoveryRequestDTO;
import com.recetas.recetasapp.dto.ResetPasswordDto;
import com.recetas.recetasapp.repository.UsuarioRepository;
import com.recetas.recetasapp.repository.AlumnoRepository;
import com.recetas.recetasapp.service.EmailService;
import com.recetas.recetasapp.service.UsuarioService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlumnoRepository alumnoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // (opcional) para almacenar códigos en memoria si lo requieres
    private Map<String, String> codigosPorEmail = new HashMap<>();

    @Autowired
    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            AlumnoRepository alumnoRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.alumnoRepository = alumnoRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public String recuperarContraseña(String mail) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(mail);
        if (optionalUsuario.isEmpty()) {
            return "No se encontró un usuario con ese mail.";
        }
        return "Se envió un correo de recuperación a: " + mail;
    }

    @Override
    @Transactional
    public Alumno convertirEnAlumno(Long idUsuario, AlumnoActualizarDTO datos) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setRol(Rol.ALUMNO);
        usuarioRepository.save(usuario);

        Alumno alumno = alumnoRepository.findByUsuarioId(idUsuario)
                .orElse(new Alumno());

        alumno.setUsuario(usuario);
        alumno.setNumeroTarjeta(datos.getNumeroTarjeta());
        alumno.setTipoTarjeta(datos.getTipoTarjeta());
        alumno.setVencimiento(datos.getVencimiento());
        alumno.setCodigoSeguridad(datos.getCodigoSeguridad());
        alumno.setDniFrente(datos.getDniFrente());
        alumno.setDniDorso(datos.getDniDorso());
        alumno.setNumeroTramite(datos.getNumeroTramite());

        return alumnoRepository.save(alumno);
    }

    @Override
    public String resetearContraseña(ResetPasswordDto datos) {
        Usuario usuario = usuarioRepository.findByEmail(datos.getEmail())
                .orElseThrow(() -> new RuntimeException("No se encontró un usuario con ese mail"));

        usuario.setPassword(passwordEncoder.encode(datos.getNuevaPassword()));
        usuarioRepository.save(usuario);

        return "Contraseña reseteada correctamente";
    }

    @Override
    @Transactional
    public void confirmarCuentaConCodigo(ConfirmacionCodigoDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        "Usuario no encontrado con el correo: " + dto.getEmail()));

        if (!usuario.getCodigoConfirmacion().equals(dto.getCodigo())) {
            usuario.setHabilitado(false);
            usuarioRepository.save(usuario);
            throw new RuntimeException("Código de confirmación inválido");
        }

        if (usuario.getCodigoExpira() != null && usuario.getCodigoExpira().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El código ha expirado. Solicite uno nuevo.");
        }

        usuario.setHabilitado(true);
        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario getUserById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void enviarCodigoRecuperacion(RecoveryRequestDTO request) {
        String email = request.getEmail();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "No existe un usuario con ese email."));

        String codigo = String.valueOf(new Random().nextInt(9000) + 1000);
        usuario.setCodigoConfirmacion(codigo);
        usuario.setCodigoExpira(LocalDateTime.now().plusHours(24));
        usuarioRepository.save(usuario);

        String asunto = "Código de recuperación";
        String texto = "Tu código de recuperación es: " + codigo + "\n\n"
                + "Este código es válido por 24 horas.";
        emailService.enviarEmail(email, asunto, texto);
    }

    @Override
    public boolean verificarCodigo(String email, String codigo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) return false;

        Usuario usuario = usuarioOpt.get();
        if (usuario.getCodigoExpira() == null
                || usuario.getCodigoExpira().isBefore(LocalDateTime.now())) {
            return false;
        }

        return codigo.equals(usuario.getCodigoConfirmacion());
    }

    @Override
    public String reenviarCodigoConfirmacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "No existe un usuario con ese email."));

        String nuevoCodigo = String.valueOf(new Random().nextInt(9000) + 1000);
        usuario.setCodigoConfirmacion(nuevoCodigo);
        usuario.setCodigoExpira(LocalDateTime.now().plusHours(24));
        usuarioRepository.save(usuario);

        String asunto = "Nuevo código de recuperación";
        String texto = "Se ha generado un nuevo código de recuperación: "
                + nuevoCodigo + "\n\n"
                + "Este código es válido por 24 horas.";
        emailService.enviarEmail(email, asunto, texto);
        return "Nuevo código enviado correctamente.";
    }

    @Override
    public Usuario getUserByAlias(String alias) {
        return usuarioRepository.findByAlias(alias)
                .orElseThrow(() -> new UserNotFoundException(
                        "Usuario no encontrado con alias: " + alias));
    }

    // Nuevos métodos para validación de alias/email
    @Override
    public boolean aliasExists(String alias) {
        return usuarioRepository.existsByAlias(alias);
    }

    @Override
    public boolean emailExists(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
