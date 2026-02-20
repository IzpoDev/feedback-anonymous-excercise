package com.feedback.feedback.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ForgotPasswordRequestDto {
    @NotBlank(message = "El token no puede estar vacio")
    private String token;
    @Email(message = "El email no es valido")
    private String email;
    @NotBlank(message = "El password no puede estar vacio")
    private String password;
}
