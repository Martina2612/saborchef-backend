package com.recetas.recetasapp.jwt.config;

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
                        // Endpoints públicos y de error
                        .requestMatchers("/api/auth/**").permitAll() // Registro, login, etc.
                        .requestMatchers("/api/**").permitAll() 
                        // .requestMatchers(HttpMethod.GET, "/books/**")
                        // .hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                        // .requestMatchers(HttpMethod.GET,"/books/{id}").hasAnyAuthority(Role.USER.name())
                        // .requestMatchers(HttpMethod.GET,"/books/genre/{genre}").hasAnyAuthority(Role.USER.name())
                        // .requestMatchers(HttpMethod.GET,"/books/title/{title}").hasAnyAuthority(Role.USER.name())
                        // .requestMatchers(HttpMethod.GET,"/books/author/{author}").hasAnyAuthority(Role.USER.name())
                        // .requestMatchers(HttpMethod.GET,"/books/available").hasAnyAuthority(Role.USER.name())
                        // .requestMatchers(HttpMethod.GET,"/books/ordered-by-price-asc").hasAnyAuthority(Role.USER.name())
                        // .requestMatchers(HttpMethod.GET,"/books/ordered-by-price-desc").hasAnyAuthority(Role.USER.name())
                        // .requestMatchers("/books/**").hasAnyAuthority(Role.ADMIN.name())
                        .anyRequest()
                        .authenticated())

                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
