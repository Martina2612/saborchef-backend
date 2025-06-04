package com.recetas.recetasapp.service.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.exception.auth.UserNotFoundException;
import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.dto.AlumnoActualizarDTO;
import com.recetas.recetasapp.dto.ConfirmacionCodigoDTO;
import com.recetas.recetasapp.dto.RegistroConfirmarDTO;
import com.recetas.recetasapp.dto.ResetPasswordDto;
import com.recetas.recetasapp.repository.UsuarioRepository;
import com.recetas.recetasapp.repository.AlumnoRepository;
import com.recetas.recetasapp.service.UsuarioService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlumnoRepository alumnoRepository;
    private final PasswordEncoder passwordEncoder;
    

    // Podés guardar esto en BD o cache tipo Redis, pero para ahora usamos memoria
    private Map<String, String> codigosPorEmail = new HashMap<>();

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, AlumnoRepository alumnoRepository,PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.alumnoRepository = alumnoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String recuperarContraseña(String mail) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(mail);
        if (optionalUsuario.isEmpty()) {
            return "No se encontró un usuario con ese mail.";
        }

        // En un caso real, esto generaría un token y enviaría un correo
        return "Se envió un correo de recuperación a: " + mail;
    }

    @Override
@Transactional
public Alumno convertirEnAlumno(Long idUsuario, AlumnoActualizarDTO datos) {
    Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    Alumno alumno = new Alumno();
    alumno.setUsuario(usuario);
    alumno.setNumeroTarjeta(datos.getNumeroTarjeta());
    alumno.setDniFrente(datos.getDniFrente());
    alumno.setDniDorso(datos.getDniDorso());
    alumno.setCuentaCorriente(datos.getCuentaCorriente());

    return alumnoRepository.save(alumno);
}

@Override
    public String resetearContraseña(ResetPasswordDto datos) {
        Usuario usuario = usuarioRepository.findByEmail(datos.getEmail())
            .orElseThrow(() -> new RuntimeException("No se encontró un usuario con ese mail"));

        // ✅ Encriptar la nueva contraseña antes de guardarla
        usuario.setPassword(passwordEncoder.encode(datos.getNuevaPassword()));
        usuarioRepository.save(usuario);

        return "Contraseña reseteada correctamente";
    }

@Override
@Transactional
public void confirmarCuentaConCodigo(ConfirmacionCodigoDTO dto) {
    Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el correo: " + dto.getEmail()));

    if (usuario.getCodigoConfirmacion().equals(dto.getCodigo())) {
        usuario.setHabilitado(true);
        usuarioRepository.save(usuario);
    } else {
        usuario.setHabilitado(false); // seguridad
        usuarioRepository.save(usuario);
        throw new RuntimeException("Código de confirmación inválido");
    }
}


@Override
public Usuario getUserById(Long id) {
    return usuarioRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
}

public String enviarCodigoRecuperacion(String email) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

    if (usuarioOpt.isEmpty()) {
        throw new RuntimeException("No existe un usuario con ese email.");
    }

    Usuario usuario = usuarioOpt.get();

    String codigo = String.valueOf(new Random().nextInt(9000) + 1000); // código de 4 dígitos


    // Guardamos el código en el usuario
    usuario.setCodigoConfirmacion(codigo);
    usuarioRepository.save(usuario);

    // Comentado el envío de mail por ahora
    /*
    SimpleMailMessage mensaje = new SimpleMailMessage();
    mensaje.setTo(email);
    mensaje.setSubject("Código de recuperación");
    mensaje.setText("Tu código de recuperación es: " + codigo);
    mailSender.send(mensaje);
    */

    return codigo; // Para pruebas
}

public boolean verificarCodigo(String email, String codigo) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

    if (usuarioOpt.isEmpty()) {
        return false;
    }

    Usuario usuario = usuarioOpt.get();
    return codigo.equals(usuario.getCodigoConfirmacion());
}






}

