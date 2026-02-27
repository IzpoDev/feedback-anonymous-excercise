package com.feedback.feedback.service.impl;

import com.feedback.feedback.exception.EntityNotFoundException;
import com.feedback.feedback.mapper.UserMapper;
import com.feedback.feedback.model.dto.ForgotPasswordRequestDto;
import com.feedback.feedback.model.dto.LoginRequestDto;
import com.feedback.feedback.model.dto.LoginResponseDto;
import com.feedback.feedback.model.dto.StartForgotPasswordResponseDto;
import com.feedback.feedback.model.entity.TokenPasswordResetEntity;
import com.feedback.feedback.model.entity.UserEntity;
import com.feedback.feedback.repository.TokenPasswordResetRepository;
import com.feedback.feedback.repository.UserRepository;
import com.feedback.feedback.service.AuthService;
import com.feedback.feedback.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService  {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JavaMailSender javaMailSender;
    private final TokenPasswordResetRepository tokenPasswordResetRepository;
    private final PasswordEncoder passwordEncoder;

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
                String htmlTemplate= StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                String htmlContent = htmlTemplate
                        .replace("{{email}}", email)
                        .replace("{{token}}", token);

                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(email);
                helper.setSubject("Recuperación de contraseña - Feedback App");
                helper.setText(htmlContent, true);
                //Envio del correo
                javaMailSender.send(message);
            } catch (IOException e) {
                throw new RuntimeException("Error al cargar la plantilla de correo", e);
            }
            catch (MessagingException ex){
                throw new RuntimeException("Error al crear el mensaje de correo", ex);
            }

            return new StartForgotPasswordResponseDto(email, "http://localhost:8081/auth/reset-password");
        } else {
            throw new EntityNotFoundException("Usuario no encontrado con el email " + email);
        }
    }

    @Override
    public String resetPassword(ForgotPasswordRequestDto forgotPasswordRequestDto) {
        TokenPasswordResetEntity token = tokenPasswordResetRepository.findByToken(forgotPasswordRequestDto.getToken())
                .orElseThrow(
                        () -> new EntityNotFoundException("Token de reset Password no encontrado")
        );
        if (token.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token de reset Password expirado");
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

}
