package com.feedback.feedback.service.impl;

import com.feedback.feedback.exception.EntityNotFoundException;
import com.feedback.feedback.mapper.UserMapper;
import com.feedback.feedback.model.dto.ForgotPasswordRequestDto;
import com.feedback.feedback.model.dto.LoginRequestDto;
import com.feedback.feedback.model.dto.LoginResponseDto;
import com.feedback.feedback.model.dto.StartForgotPasswordResponseDto;
import com.feedback.feedback.repository.UserRepository;
import com.feedback.feedback.service.AuthService;
import com.feedback.feedback.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

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
        return null;
    }

    @Override
    public String resetPassword(ForgotPasswordRequestDto forgotPasswordRequestDto) {
        return "";
    }


}
