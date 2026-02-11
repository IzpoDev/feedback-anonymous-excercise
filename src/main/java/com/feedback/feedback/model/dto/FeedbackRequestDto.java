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
public class FeedbackRequestDto {
    @NotBlank(message = "El contenido no puede estar vacio")
    private String content;
    @NotBlank(message = "El id del usuario destinatario no puede estar vacio")
    private Long recipientId;
}
