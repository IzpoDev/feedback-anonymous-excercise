package com.feedback.feedback.modules.auth.controller;

import com.feedback.feedback.modules.auth.controller.dto.ForgotPasswordRequestDto;
import com.feedback.feedback.modules.auth.controller.dto.LoginRequestDto;
import com.feedback.feedback.modules.auth.controller.dto.LoginResponseDto;
import com.feedback.feedback.modules.auth.controller.dto.StartForgotPasswordResponseDto;
import com.feedback.feedback.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto){
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }
    @PostMapping("/forgot-password/{email}")
    public ResponseEntity<StartForgotPasswordResponseDto> startForgotPasswordReset(@PathVariable("email") String email){
        StartForgotPasswordResponseDto response = authService.startForgotPassword(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto){
        String response = authService.resetPassword(forgotPasswordRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
