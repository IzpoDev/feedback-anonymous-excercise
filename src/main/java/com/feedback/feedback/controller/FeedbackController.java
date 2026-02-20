package com.feedback.feedback.controller;

import com.feedback.feedback.model.dto.FeedbackRequestDto;
import com.feedback.feedback.model.dto.FeedbackResponseDto;
import com.feedback.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponseDto> createFeedback(@RequestBody @Valid FeedbackRequestDto feedbackRequestDto){
        return new ResponseEntity<>(feedbackService.createFeedback(feedbackRequestDto), HttpStatus.CREATED);
    }
    @GetMapping("/{recipientId}")
    public ResponseEntity<List<FeedbackResponseDto>> getAllFeedbacks(@PathVariable Long recipientId){
           return new ResponseEntity<>(feedbackService.getAllFeedbacksByRecipientId(recipientId), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> getFeedbackById(@PathVariable Long id){
        return new ResponseEntity<>(feedbackService.getFeedbackById(id), HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> updateFeedback(@PathVariable Long id,@RequestBody @Valid FeedbackRequestDto feedbackRequestDto){
        return new ResponseEntity<>(feedbackService.updateFeedback(id, feedbackRequestDto), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFeedback(@PathVariable Long id){
        feedbackService.deleteFeedback(id);
        return new ResponseEntity<>("Feedback con id " + id + " Eliminado ", HttpStatus.OK);
    }

}
