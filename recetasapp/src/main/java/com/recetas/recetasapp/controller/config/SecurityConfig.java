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
            //Rutas para generar documentación Swagger
            .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html",
                        "/swagger-resources/**","/webjars/**").permitAll()

            // Públicos: login, registro, confirmar
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/usuarios/**").permitAll()

            // Visitantes pueden ver recetas, cursos (sin detalles)
            .requestMatchers(HttpMethod.GET, "/api/recetas/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/cursos/**").permitAll()

            // Usuarios (login requerido)
            .requestMatchers(HttpMethod.POST, "/api/recetas/**").hasAnyAuthority("USUARIO", "ALUMNO", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/recetas/**").hasAnyAuthority("USUARIO", "ALUMNO", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/recetas/**").hasAnyAuthority("USUARIO", "ALUMNO", "ADMIN")

            // Alumnos (funciones como inscribirse, ver estado de cuenta, asistir)
            .requestMatchers("/api/inscripciones/**").hasAnyAuthority("ALUMNO", "ADMIN")
            .requestMatchers("/api/asistencias/**").hasAnyAuthority("ALUMNO", "ADMIN")
            .requestMatchers("/api/cursos/{id}/asistencia").hasAnyAuthority("ALUMNO", "ADMIN")

            // Admin (gestionar todo)
            .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

            // Todo lo demás requiere autenticación
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

}
