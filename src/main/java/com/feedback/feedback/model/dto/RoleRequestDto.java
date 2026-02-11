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
public class RoleRequestDto {
    @NotBlank(message = "El nombre no peude ser vacio, nulo o estar en blanco")
    private String name;
    @NotBlank(message = "La descripcion no peude ser vacia, nula o estar en blanco")
    private String description;
}
