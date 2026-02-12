package com.feedback.feedback.model.dto;

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
