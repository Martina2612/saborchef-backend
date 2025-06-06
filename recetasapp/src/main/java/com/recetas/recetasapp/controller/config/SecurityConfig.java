package com.recetas.recetasapp.controller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(req -> req
            // 🔥 PRIMERO: Endpoints de password recovery (MÁS ESPECÍFICOS)
            .requestMatchers(HttpMethod.POST, "/api/usuarios/password/send-code").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/usuarios/password/verify-code").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/usuarios/password/reset").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/usuarios/codigo/reenviar").permitAll()
            // Públicos: login, registro, confirmar
            .requestMatchers("/api/auth/**").permitAll()
            
            // 🔥 DESPUÉS: Regla más general de usuarios
            .requestMatchers("/api/usuarios/**").permitAll()

            // Visitantes pueden ver recetas, cursos (sin detalles)
            .requestMatchers(HttpMethod.GET, "/api/recetas/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/cursos/**").permitAll()

            // Usuarios (login requerido)
            .requestMatchers(HttpMethod.POST, "/api/recetas/**").hasAnyAuthority("ROLE_USUARIO", "ROLE_ALUMNO", "ROLE_ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/recetas/**").hasAnyAuthority("ROLE_USUARIO", "ROLE_ALUMNO", "ROLE_ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/recetas/**").hasAnyAuthority("ROLE_USUARIO", "ROLE_ALUMNO", "ROLE_ADMIN")

            // Alumnos (funciones como inscribirse, ver estado de cuenta, asistir)
            .requestMatchers("/api/inscripciones/**").hasAnyAuthority("ROLE_USUARIO", "ROLE_ALUMNO", "ROLE_ADMIN")
            .requestMatchers("/api/asistencias/**").hasAnyAuthority("ROLE_USUARIO", "ROLE_ALUMNO", "ROLE_ADMIN")
            .requestMatchers("/api/cursos/{id}/asistencia").hasAnyAuthority("ROLE_USUARIO", "ROLE_ALUMNO", "ROLE_ADMIN")

            // Admin (gestionar todo)
            .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

            // Todo lo demás requiere autenticación
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}




}
