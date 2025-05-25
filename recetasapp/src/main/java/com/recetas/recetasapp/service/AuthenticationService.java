package com.recetas.recetasapp.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.recetas.recetasapp.controller.auth.AuthenticationRequest;
import com.recetas.recetasapp.controller.auth.AuthenticationResponse;
import com.recetas.recetasapp.controller.auth.RegisterRequest;
import com.recetas.recetasapp.controller.config.JwtService;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.exception.auth.AliasAlreadyExistsException;
import com.recetas.recetasapp.exception.auth.EmailAlreadyExistsException;
import com.recetas.recetasapp.exception.auth.InvalidCredentialsException;
import com.recetas.recetasapp.exception.auth.UserNotFoundException;
import com.recetas.recetasapp.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("El correo electrónico ya está registrado.");
        }
        if (repository.existsByAlias(request.getAlias())) {
            throw new AliasAlreadyExistsException("El alias ya está registrado.");
        }

        var user = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .alias(request.getAlias())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRole())
                .habilitado(false)
                .build();

        repository.save(user);
        
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .userId(user.getId())
                .role(user.getRol().toString())
                .email(user.getEmail())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
    try {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getAlias(),
                        request.getPassword())
        );
    } catch (BadCredentialsException ex) {
        throw new InvalidCredentialsException("Nombre de usuario o contraseña incorrectos.");
    }

    var usuario = repository.findByAlias(request.getAlias())
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el alias: " + request.getAlias()));

    if (!usuario.getHabilitado()) {
        throw new InvalidCredentialsException("La cuenta no está habilitada. Por favor, confirma tu registro.");
}


    var jwtToken = jwtService.generateToken(usuario);

    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .userId(usuario.getId())
            .role(usuario.getRol().toString())
            .email(usuario.getEmail())
            .build();
}


    public Usuario getUserByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el correo: " + email));
    }

    public Usuario getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el id: " + id));
    }

    public void updateUser(Usuario usuario) {
        repository.save(usuario);
    }
}
