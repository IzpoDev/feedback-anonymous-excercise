package com.feedback.feedback.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequestDto {
    @NotBlank(message = "El username no puede estar vacio")
    private String username;
    @NotBlank(message = "El password no puede estar vacio")
    private String password;
}
