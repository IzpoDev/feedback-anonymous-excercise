package com.feedback.feedback.modules.auth.controller.dto;

import com.feedback.feedback.modules.user.model.dto.UserResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class LoginResponseDto {
    private String token;
    private UserResponseDto user;
}
