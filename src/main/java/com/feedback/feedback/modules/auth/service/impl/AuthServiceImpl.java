package com.feedback.feedback.modules.auth.service.impl;


import com.feedback.feedback.common.exception.BusinessLogicException;
import com.feedback.feedback.common.exception.EntityNotFoundException;
import com.feedback.feedback.common.mapper.UserMapper;
import com.feedback.feedback.common.util.JwtUtil;
import com.feedback.feedback.modules.auth.controller.dto.ConfigurationResponse;
import com.feedback.feedback.modules.auth.controller.dto.ForgotPasswordRequestDto;
import com.feedback.feedback.modules.auth.controller.dto.LoginRequestDto;
import com.feedback.feedback.modules.auth.controller.dto.LoginResponseDto;
import com.feedback.feedback.modules.auth.controller.dto.StartForgotPasswordResponseDto;

import com.feedback.feedback.modules.auth.entity.TokenPasswordResetEntity;
import com.feedback.feedback.modules.auth.repository.TokenPasswordResetRepository;
import com.feedback.feedback.modules.user.model.entity.UserEntity;
import com.feedback.feedback.modules.user.repository.UserRepository;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.env.Environment;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import com.feedback.feedback.modules.auth.service.AuthService;

import java.io.IOException;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${resend.api.key}")
    private String resendApiKey;
    @Value("${web.origin}")
    private String urlOrigin;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    //private final JavaMailSender javaMailSender;
    private final TokenPasswordResetRepository tokenPasswordResetRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;
    private final DataSource dataSource;

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.username(),loginRequestDto.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assert userDetails != null;
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(auth -> auth.replace("ROLE_", ""))
                .findFirst()
                .orElseThrow();
        String token = jwtUtil.generateToken(userDetails.getUsername(), role);

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUser(UserMapper.toDto(
                userRepository.findByUsernameAndActive(
                        loginRequestDto.username(),Boolean.TRUE).orElseThrow(
                                ()-> new EntityNotFoundException("Usuario no encontrado")
                )
            )
        );
        return response;
    }

    @Override
    public StartForgotPasswordResponseDto startForgotPassword(String email) {
        if (userRepository.existsByEmail(email)) {
            String token = UUID.randomUUID().toString();
            UserEntity userTemp = userRepository.findByEmail(email).orElseThrow(
                    ()-> new EntityNotFoundException("Usuario no encontrado con el email " + email)
            );
            //Token creado y almacenado en la base de datos con su fecha de expiracion
            TokenPasswordResetEntity tokenInfo = new TokenPasswordResetEntity();
            tokenInfo.setUsed(Boolean.FALSE);
            tokenInfo.setToken(token);
            tokenInfo.setUser(userTemp);
            tokenInfo.setExpireDate(LocalDateTime.now().plusHours(1));
            tokenPasswordResetRepository.save(tokenInfo);

            //Creacion del mensaje en formato html para enviar el correo
            try {
                ClassPathResource resource = new ClassPathResource("templates/index_mail.html");
                String htmlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                String messageContent = htmlContent
                        .replace("{{email}}", email)
                        .replace("{{token}}", token)
                        .replace("{{url}}", urlOrigin);

                //  2. Instanciamos Resend y armamos el correo
                Resend resend = new Resend(resendApiKey);

                CreateEmailOptions params = CreateEmailOptions.builder()
                        .from("Feedback App <no-reply@automasilabo.space>")
                        .to(email)
                        .subject("Recuperación de contraseña - Feedback App")
                        .html(messageContent)
                        .build();

                // 3. Enviamos el correo vía HTTP
                resend.emails().send(params);

            } catch (IOException e) {
                throw new RuntimeException("Error al cargar la plantilla de correo", e);
            } catch (ResendException e) {
                throw new RuntimeException("Error en la API de Resend al enviar el correo", e);
            }

            return new StartForgotPasswordResponseDto(email, "http://feedback-api.automasilabo.space/auth/reset-password");
        } else {
            throw new EntityNotFoundException("Usuario no encontrado con el email " + email);
        }
    }

    @Override
    public String resetPassword(ForgotPasswordRequestDto forgotPasswordRequestDto) {

        TokenPasswordResetEntity token = tokenPasswordResetRepository.findByToken(forgotPasswordRequestDto.getToken())
                .orElseThrow(
                        () -> new BusinessLogicException("Token de reset Password no encontrado o ya fue usado")
        );
        if (token.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new BusinessLogicException("Token de reset Password expirado");
        }
        UserEntity user = userRepository.findByEmail(forgotPasswordRequestDto.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("Usuario no encontrado con el email " + forgotPasswordRequestDto.getEmail())
        );
        user.setPassword(passwordEncoder.encode(forgotPasswordRequestDto.getPassword()));
        userRepository.save(user);
        token.setUsed(Boolean.TRUE);
        tokenPasswordResetRepository.save(token);
        return "El usuario " + user.getUsername() + " restablecio su contraseña con exito";
    }

    @Override
    public ConfigurationResponse getConfiguration() {
        ConfigurationResponse response = new ConfigurationResponse();
        response.email_status = getEmailStatus();
        response.jwt_status = getJwtStatus();
        response.db_status = getDatabaseStatus();
        return response;
    }

    private String getEmailStatus() {

        String resendKey = environment.getProperty("resend.api.key");

        if (isMissingValue(resendKey)) {
            return "MISSING_RESEND_KEY";
        }
        return "OK_RESEND_API";
    }

    private String getJwtStatus() {
        String jwtSecret = environment.getProperty("spring.jwt.secret");
        String jwtExpiration = environment.getProperty("spring.jwt.expiration");

        if (isMissingValue(jwtSecret) || isMissingValue(jwtExpiration)) {
            return "MISSING";
        }
        return "OK";
    }

    private String getDatabaseStatus() {
        try (var connection = dataSource.getConnection()) {
            return connection.isValid(2) ? "OK" : "INVALID_CONNECTION";
        } catch (SQLException ex) {
            return "CONNECTION_ERROR";
        }
    }

    private boolean isMissingValue(String value) {
        return value == null || value.isBlank() || value.contains("${");
    }

}
