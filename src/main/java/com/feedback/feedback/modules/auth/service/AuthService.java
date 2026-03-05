package com.feedback.feedback.modules.auth.service;

import com.feedback.feedback.modules.auth.controller.dto.ForgotPasswordRequestDto;
import com.feedback.feedback.modules.auth.controller.dto.LoginRequestDto;
import com.feedback.feedback.modules.auth.controller.dto.LoginResponseDto;
import com.feedback.feedback.modules.auth.controller.dto.StartForgotPasswordResponseDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto loginRequestDto);
    StartForgotPasswordResponseDto startForgotPassword(String email);
    String resetPassword(ForgotPasswordRequestDto forgotPasswordRequestDto);
}
