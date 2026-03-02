package com.feedback.feedback.service;


import com.feedback.feedback.model.dto.FeedbackRequestDto;
import com.feedback.feedback.model.dto.FeedbackResponseDto;
import com.feedback.feedback.model.dto.UserResponseDto;

import java.util.List;

public interface FeedbackService {
    FeedbackResponseDto createFeedback(FeedbackRequestDto feedbackRequestDto);
    FeedbackResponseDto getFeedbackById(Long id);
    List<FeedbackResponseDto> getAllFeedbacks();
    List<FeedbackResponseDto> getAllFeedbacksByRecipientId(Long recipientId);
    FeedbackResponseDto updateFeedback(Long id, FeedbackRequestDto feedbackRequestDto);
    List<UserResponseDto> getOwners();
    void deleteFeedback(Long id);
}
