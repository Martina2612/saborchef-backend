package com.recetas.recetasapp.service.serviceimpl;

import com.recetas.recetasapp.dto.AlumnoActualizarDTO;
import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.entity.Rol;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.repository.AlumnoRepository;
import com.recetas.recetasapp.repository.UsuarioRepository;
import com.recetas.recetasapp.service.CodigoService;
import com.recetas.recetasapp.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final AlumnoRepository alumnoRepo;
    private final CodigoService codigoService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario iniciarRegistro(String mail, String alias) {
        if (usuarioRepo.existsByMail(mail) || usuarioRepo.existsByNickname(alias)) {
            throw new RuntimeException("Mail o alias ya en uso");
        }

        Usuario user = new Usuario();
        user.setMail(mail);
        user.setNickname(alias);
        user.setRol(Rol.USUARIO);
        user.setHabilitado(false);

        usuarioRepo.save(user);
        codigoService.enviarCodigoRegistro(mail);

        return user;
    }

    @Override
    public boolean confirmarRegistro(String mail, String codigo, String password, String nombre, String apellido) {
        if (!codigoService.verificarCodigo(mail, codigo)) return false;

        Usuario user = usuarioRepo.findByEmail(mail)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(password));
        user.setNombre(nombre);
        user.setApellido(apellido);
        user.setHabilitado(true);

        usuarioRepo.save(user);
        return true;
    }

    @Override
    public Usuario login(String mail, String password) {
        Usuario user = usuarioRepo.findByEmail(mail)
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!user.getHabilitado() || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas o usuario no habilitado");
        }

        return user;
    }

    @Override
    public Alumno convertirEnAlumno(Long idUsuario, AlumnoActualizarDTO datos) {
        Usuario user = usuarioRepo.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Alumno alumno = new Alumno();
        alumno.setUsuario(user);
        alumno.setNumeroTarjeta(datos.getNumeroTarjeta());
        alumno.setDniFrente(datos.getDniFrente());
        alumno.setDniDorso(datos.getDniDorso());
        alumno.setCuentaCorriente(datos.getCuentaCorriente());
        alumno.setNombre(datos.getNombre());

        user.setRol(Rol.ALUMNO);
        usuarioRepo.save(user);
        return alumnoRepo.save(alumno);
    }
}
