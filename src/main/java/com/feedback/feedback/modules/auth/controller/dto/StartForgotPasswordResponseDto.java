package com.feedback.feedback.modules.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StartForgotPasswordResponseDto {

    private String email;
    private String link;
}
