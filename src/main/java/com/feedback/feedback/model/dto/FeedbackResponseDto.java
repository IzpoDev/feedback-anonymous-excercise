package com.feedback.feedback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeedbackResponseDto {

    private Long id;
    private String content;
    private String recipient;
    private LocalDateTime createdAt;
}
