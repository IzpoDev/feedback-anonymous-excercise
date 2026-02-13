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
public class PrivilegeRequestDto {
    @NotBlank(message = "El nombre no puede estar vacio")
    private String name;
    @NotBlank(message = "La descripcion no puede estar vacia")
    private String description;
}
