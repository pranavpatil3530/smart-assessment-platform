package com.smartassessment.service;

import com.smartassessment.dto.attempt.AttemptHistoryDto;
import com.smartassessment.dto.attempt.ExamResultResponseDto;
import com.smartassessment.dto.attempt.StartExamResponseDto;
import com.smartassessment.dto.attempt.SubmitExamRequestDto;
import com.smartassessment.dto.common.PagedResponse;

public interface ExamAttemptService {
    StartExamResponseDto startOrResumeExam(Long examId, Long userId);
    ExamResultResponseDto submitExam(Long attemptId, Long userId, SubmitExamRequestDto submitDto, boolean isAutoSubmit);
    ExamResultResponseDto getAttemptResult(Long attemptId, Long userId);
    PagedResponse<AttemptHistoryDto> getStudentAttemptHistory(Long userId, int page, int size);
}
