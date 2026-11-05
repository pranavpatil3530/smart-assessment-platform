package com.smartassessment.controller;

import com.smartassessment.dto.common.ApiResponse;
import com.smartassessment.dto.question.QuestionRequestDto;
import com.smartassessment.dto.question.QuestionResponseDto;
import com.smartassessment.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/exams/{examId}/questions")
    public ResponseEntity<ApiResponse<QuestionResponseDto>> addQuestion(
            @PathVariable Long examId,
            @Valid @RequestBody QuestionRequestDto requestDto) {
        QuestionResponseDto created = questionService.addQuestionToExam(examId, requestDto);
        return new ResponseEntity<>(ApiResponse.success(created, "Question added successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<QuestionResponseDto>> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequestDto requestDto) {
        QuestionResponseDto updated = questionService.updateQuestion(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Question updated successfully"));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Question deleted successfully"));
    }

    @GetMapping("/exams/{examId}/questions")
    public ResponseEntity<ApiResponse<List<QuestionResponseDto>>> getExamQuestions(@PathVariable Long examId) {
        List<QuestionResponseDto> questions = questionService.getQuestionsByExamId(examId);
        return ResponseEntity.ok(ApiResponse.success(questions, "Questions retrieved successfully"));
    }
}
