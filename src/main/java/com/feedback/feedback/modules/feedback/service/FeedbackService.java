package com.feedback.feedback.modules.feedback.service;


import com.feedback.feedback.modules.feedback.model.dto.FeedbackRequestDto;
import com.feedback.feedback.modules.feedback.model.dto.FeedbackResponseDto;
import com.feedback.feedback.modules.user.model.dto.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FeedbackService {
    FeedbackResponseDto createFeedback(FeedbackRequestDto feedbackRequestDto);
    FeedbackResponseDto createFeedbackWithAudio(MultipartFile audio, Long recipientId);
    FeedbackResponseDto getFeedbackById(Long id);
    List<FeedbackResponseDto> getAllFeedbacks();
    List<FeedbackResponseDto> getAllFeedbacksByRecipientId(Long recipientId);
    FeedbackResponseDto updateFeedback(Long id, FeedbackRequestDto feedbackRequestDto);
    List<UserResponseDto> getOwners();
    void deleteFeedback(Long id);
}
