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
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/auth/login",
                                "/users/",
                                "/users/admin/**",
                                "/feedbacks/**"
                        ).permitAll()
                        .requestMatchers(
                                "/roles/**"
                        ).hasAnyRole("ADMIN", "OWNER")
                        .requestMatchers(HttpMethod.PUT,
                                "/feedbacks/**")
                        .hasAuthority("UPDATE_FEEDBACK")
                        .requestMatchers(HttpMethod.DELETE,
                                "/feedbacks/**")
                        .hasAuthority("DELETE_FEEDBACK")
                        .requestMatchers(HttpMethod.GET, "/feedbacks/**")
                        .hasAuthority("READ_FEEDBACK")
                        .requestMatchers(HttpMethod.GET, "/feedbacks/owner/**")
                        .hasAnyRole("ADMIN", "OWNER")
                        .requestMatchers(
                                "/privileges/**"
                        ).hasRole("ADMIN")
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