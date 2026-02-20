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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1. Habilitar CORS nativo y deshabilitar CSRF
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 2. Configurar las reglas de autorización
        http.authorizeHttpRequests(
                requests -> requests
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/auth/login",
                                "/users/", // Solo registro de usuarios normales
                                "/feedbacks/**" // Feedback anónimo
                        ).permitAll()
                        //  SEGURIDAD CORREGIDA: Solo un ADMIN puede crear a otro ADMIN
                        .requestMatchers(HttpMethod.POST, "/users/admin/**").hasRole("ADMIN")
                        .requestMatchers("/roles/**", "/privileges/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/feedbacks/**").hasAuthority("UPDATE_FEEDBACK")
                        .requestMatchers(HttpMethod.DELETE, "/feedbacks/**").hasAuthority("DELETE_FEEDBACK")
                        .requestMatchers(HttpMethod.GET, "/feedbacks/**").hasAuthority("READ_FEEDBACK")
                        .requestMatchers(HttpMethod.GET, "/feedbacks/owner/**").hasAnyRole("ADMIN", "OWNER")
                        .anyRequest().authenticated()
        );
        // 3. Deshabilitar el formulario de login por defecto de Spring.
        http.formLogin(AbstractHttpConfigurer::disable);

        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        http.exceptionHandling( e -> e.accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();

    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*")); // Permitir todas las fuentes
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("authorization", "content-type", "x-requested-with"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
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