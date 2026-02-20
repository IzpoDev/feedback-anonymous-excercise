package com.feedback.feedback.service.impl;

import com.feedback.feedback.exception.EntityNotFoundException;
import com.feedback.feedback.mapper.FeedbackMapper;
import com.feedback.feedback.model.dto.FeedbackRequestDto;
import com.feedback.feedback.model.dto.FeedbackResponseDto;
import com.feedback.feedback.model.entity.FeedbackEntity;
import com.feedback.feedback.repository.FeedbackRepository;
import com.feedback.feedback.repository.UserRepository;
import com.feedback.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Override
    public FeedbackResponseDto createFeedback(FeedbackRequestDto feedbackRequestDto) {
        FeedbackEntity feedbackEntity = FeedbackMapper.toEntity(feedbackRequestDto);
        feedbackEntity.setRecipient(userRepository.getReferenceById(feedbackRequestDto.getRecipientId()));
        feedbackEntity.setActive(true);
        feedbackEntity = feedbackRepository.save(feedbackEntity);
        return FeedbackMapper.toDto(feedbackEntity);
    }

    @Override
    public FeedbackResponseDto getFeedbackById(Long id) {
        return FeedbackMapper.toDto(feedbackRepository.getReferenceById(id));
    }

    @Override
    public List<FeedbackResponseDto> getAllFeedbacksByRecipientId(Long recipientId) {
        return FeedbackMapper.toListDto(feedbackRepository.findAllByRecipientId(recipientId));
    }

    @Override
    public FeedbackResponseDto updateFeedback(Long id, FeedbackRequestDto feedbackRequestDto) {
        FeedbackEntity feedbackEntity = feedbackRepository.getReferenceById(id);
        feedbackEntity.setContent(feedbackRequestDto.getContent());
        feedbackRepository.save(feedbackEntity);
        return FeedbackMapper.toDto(feedbackEntity);
    }

    @Override
    public void deleteFeedback(Long id) {
        FeedbackEntity feedbackEntity = feedbackRepository.getReferenceById(id);
        feedbackEntity.setActive(false);
        feedbackRepository.save(feedbackEntity);
    }
}
