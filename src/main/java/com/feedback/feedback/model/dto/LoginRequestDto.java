package com.feedback.feedback.model.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto (@NotBlank(message = "El username no puede estar vacío") String username,
                               @NotBlank(message = "El password no puede estar vacío") String password) {}
