package com.feedback.feedback.modules.feedback.service.impl;

import com.feedback.feedback.common.mapper.FeedbackMapper;
import com.feedback.feedback.common.mapper.UserMapper;
import com.feedback.feedback.common.util.DeepgramService;
import com.feedback.feedback.modules.feedback.model.dto.FeedbackRequestDto;
import com.feedback.feedback.modules.feedback.model.dto.FeedbackResponseDto;
import com.feedback.feedback.modules.user.model.dto.UserResponseDto;
import com.feedback.feedback.modules.feedback.model.entity.FeedbackEntity;
import com.feedback.feedback.modules.feedback.repository.FeedbackRepository;
import com.feedback.feedback.modules.user.repository.UserRepository;
import com.feedback.feedback.modules.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final DeepgramService deepgramService;

    @Override
    public FeedbackResponseDto createFeedback(FeedbackRequestDto feedbackRequestDto) {
        if (feedbackRequestDto.getContent().isEmpty()) {
            throw new RuntimeException("El contenido del feedback no puede estar vacío");
        }
        FeedbackEntity feedbackEntity = FeedbackMapper.toEntity(feedbackRequestDto);
        feedbackEntity.setRecipient(userRepository.getReferenceById(feedbackRequestDto.getRecipientId()));
        feedbackEntity.setActive(true);
        feedbackEntity = feedbackRepository.save(feedbackEntity);
        return FeedbackMapper.toDto(feedbackEntity);
    }

    @Override
    public FeedbackResponseDto createFeedbackWithAudio(MultipartFile audio, Long recipientId) {
        if (audio == null || audio.isEmpty()){
            throw new RuntimeException("Error: El audio no puede ser nulo o estar vacío");
        }
        String content = deepgramService.transcribeAudio(audio);
        FeedbackRequestDto request = new FeedbackRequestDto(content,recipientId);
        return createFeedback(request);
    }


    @Override
    public FeedbackResponseDto getFeedbackById(Long id) {
        return FeedbackMapper.toDto(feedbackRepository.getReferenceById(id));
    }

    @Override
    public List<FeedbackResponseDto> getAllFeedbacks() {
        return FeedbackMapper.toListDto(feedbackRepository.findAll());
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
    public List<UserResponseDto> getOwners() {
        return userRepository.findByRoleOwnerAndActiveTrue()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public void deleteFeedback(Long id) {
        FeedbackEntity feedbackEntity = feedbackRepository.getReferenceById(id);
        feedbackEntity.setActive(false);
        feedbackRepository.save(feedbackEntity);
    }
}
