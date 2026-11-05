package com.smartassessment.service;

import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.dto.exam.ExamDetailResponseDto;
import com.smartassessment.dto.exam.ExamRequestDto;
import com.smartassessment.dto.exam.ExamResponseDto;

public interface ExamService {
    ExamResponseDto createExam(ExamRequestDto requestDto);
    ExamResponseDto updateExam(Long id, ExamRequestDto requestDto);
    void deleteExam(Long id);
    ExamDetailResponseDto getExamById(Long id);
    ExamResponseDto togglePublish(Long id, boolean published);

    PagedResponse<ExamResponseDto> getAllExams(Long categoryId, String keyword, int page, int size, String sortBy, String sortDir);
    PagedResponse<ExamResponseDto> getPublishedExams(Long categoryId, String keyword, int page, int size, String sortBy, String sortDir);
}
