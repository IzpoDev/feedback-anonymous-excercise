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
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JavaMailSender javaMailSender;
    private final TokenPasswordResetRepository tokenPasswordResetRepository;

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
                userRepository.findUsernameAndActive(
                        loginRequestDto.username(),Boolean.TRUE).orElseThrow(
                                ()-> new EntityNotFoundException("Usuario no encontrado")
                )
            )
        );
        return response;
    }

    @Override
    public StartForgotPasswordResponseDto startForgotPassword(String email) {
        if (userRepository.existUserByEmail(email)) {
            String token = UUID.randomUUID().toString();
            UserEntity userTemp = userRepository.findByEmail(email).orElseThrow(
                    ()-> new EntityNotFoundException("Usuario no encontrado con el email " + email)
            );
            TokenPasswordResetEntity tokenInfo = new TokenPasswordResetEntity();
            tokenInfo.setUsed(Boolean.FALSE);
            tokenInfo.setToken(token);
            tokenInfo.setUser(userTemp);
            tokenInfo.setExpireDate(LocalDateTime.now().plusHours(1));
            tokenPasswordResetRepository.save(tokenInfo);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Recuperacion de contraseña - Feedback App");
            message.setText("Ingresa aqui 'http://localhost:8081/auth/reset-password' el token " +
                    token + " & email: " + email);
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
        user.setPassword(forgotPasswordRequestDto.getPassword());
        userRepository.save(user);
        token.setUsed(Boolean.TRUE);
        return "El usuario " + user.getUsername() + " se ha reseteado su contraseña con exito";
    }

}
