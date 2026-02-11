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
public class UserRequestDto {
    @NotBlank(message = "El username no peude ser vacio, nulo o estar en blanco")
    private String username;
    @Email(message = "Este campo debe tener un formato de email example@...com")
    private String email;
    @NotBlank(message = "El password no peue ser nulo")
    private String password;
}
