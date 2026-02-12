package com.feedback.feedback.service;

import com.feedback.feedback.model.dto.ForgotPasswordRequestDto;
import com.feedback.feedback.model.dto.LoginRequestDto;
import com.feedback.feedback.model.dto.LoginResponseDto;
import com.feedback.feedback.model.dto.StartForgotPasswordResponseDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto loginRequestDto);
    StartForgotPasswordResponseDto startForgotPassword(String email);
    String resetPassword(ForgotPasswordRequestDto forgotPasswordRequestDto);
}
