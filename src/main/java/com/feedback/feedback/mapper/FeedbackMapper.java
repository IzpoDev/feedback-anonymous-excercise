package com.feedback.feedback.mapper;

import com.feedback.feedback.model.dto.FeedbackRequestDto;
import com.feedback.feedback.model.dto.FeedbackResponseDto;
import com.feedback.feedback.model.entity.FeedbackEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeedbackMapper {

    public static FeedbackEntity toEntity(FeedbackRequestDto feedbackRequestDto){
        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.setContent(feedbackRequestDto.getContent());
        return feedbackEntity;
    }
    public static FeedbackResponseDto toDto(FeedbackEntity feedbackEntity){
        FeedbackResponseDto feedbackResponseDto = new FeedbackResponseDto();
        feedbackResponseDto.setContent(feedbackEntity.getContent());
        feedbackResponseDto.setId(feedbackEntity.getId());
        feedbackResponseDto.setCreatedAt(feedbackEntity.getCreatedAt());
        feedbackResponseDto.setRecipient(feedbackEntity.getRecipient().getUsername());
        return feedbackResponseDto;
    }
    public static List<FeedbackResponseDto> toListDto(List<FeedbackEntity> feedbackEntities){
        return feedbackEntities.stream()
                .map(FeedbackMapper::toDto)
                .toList();
    }
}
