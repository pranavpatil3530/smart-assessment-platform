package com.smartassessment.service;

import com.smartassessment.dto.question.QuestionRequestDto;
import com.smartassessment.dto.question.QuestionResponseDto;

import java.util.List;

public interface QuestionService {
    QuestionResponseDto addQuestionToExam(Long examId, QuestionRequestDto requestDto);
    QuestionResponseDto updateQuestion(Long questionId, QuestionRequestDto requestDto);
    void deleteQuestion(Long questionId);
    List<QuestionResponseDto> getQuestionsByExamId(Long examId);
}
