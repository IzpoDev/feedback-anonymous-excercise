package com.feedback.feedback.config;

import com.feedback.feedback.exception.CustomAccessDeniedHandler;
import com.feedback.feedback.exception.CustomBasicAuthenticationEntryPoint;
import com.feedback.feedback.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //1. Deshabilitar el filtro CSRF y añadir nustro filtro JWT al pricipio del encabezado
        http.csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        //2. Configurar las reglas de autorizacion de las peticiones http
        http.authorizeHttpRequests(
                requests -> requests
                        // A: Los endpoints publicos registrar owners para recibir feedbacks y logear y encontrar su feedbacks,
                        // ademas para registrar el feedback anonimo

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/auth/login",
                                "/users/",
                                "/feedbacks/**"
                        ).permitAll()
                        // B: Los endpoints de admin
                        .requestMatchers(
                                "/roles/**"
                        ).hasAnyRole("ADMIN", "OWNER")

                        // Admin solo podra actualizar feedbacks y eliminarlos
                        .requestMatchers(HttpMethod.PUT,
                                "/feedbacks/**")
                        .hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/feedbacks/**")
                        .hasAnyRole("ADMIN")
                        //El ownner como el admin podra ver todos los feedbacks que le correspondan pero el admin de manera global
                        .requestMatchers(HttpMethod.GET, "/feedbacks/**")
                        .hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/feedbacks/owner/**")
                        .hasAnyRole("ADMIN", "OWNER")

                        .anyRequest().authenticated()
        );
        // 3. Deshabilitar el formulario de login por defecto de Spring.
        http.formLogin(AbstractHttpConfigurer::disable);

        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        http.exceptionHandling( e -> e.accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();

    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}