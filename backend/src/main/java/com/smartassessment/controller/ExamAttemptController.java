package com.smartassessment.controller;

import com.smartassessment.dto.attempt.AttemptHistoryDto;
import com.smartassessment.dto.attempt.ExamResultResponseDto;
import com.smartassessment.dto.attempt.StartExamResponseDto;
import com.smartassessment.dto.attempt.SubmitExamRequestDto;
import com.smartassessment.dto.common.ApiResponse;
import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.security.CurrentUser;
import com.smartassessment.security.UserPrincipal;
import com.smartassessment.service.ExamAttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_ADMIN')")
public class ExamAttemptController {

    private final ExamAttemptService examAttemptService;

    public ExamAttemptController(ExamAttemptService examAttemptService) {
        this.examAttemptService = examAttemptService;
    }

    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<ApiResponse<StartExamResponseDto>> startExam(
            @PathVariable Long examId,
            @CurrentUser UserPrincipal currentUser) {
        StartExamResponseDto response = examAttemptService.startOrResumeExam(examId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Examination session started successfully"));
    }

    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<ApiResponse<ExamResultResponseDto>> submitExam(
            @PathVariable Long attemptId,
            @RequestParam(defaultValue = "false") boolean autoSubmit,
            @RequestBody(required = false) SubmitExamRequestDto submitDto,
            @CurrentUser UserPrincipal currentUser) {
        ExamResultResponseDto result = examAttemptService.submitExam(attemptId, currentUser.getId(), submitDto, autoSubmit);
        String message = autoSubmit ? "Examination automatically submitted upon timer expiration" : "Examination submitted successfully";
        return ResponseEntity.ok(ApiResponse.success(result, message));
    }

    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<ApiResponse<ExamResultResponseDto>> getAttemptResult(
            @PathVariable Long attemptId,
            @CurrentUser UserPrincipal currentUser) {
        ExamResultResponseDto result = examAttemptService.getAttemptResult(attemptId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(result, "Examination result retrieved successfully"));
    }

    @GetMapping("/attempts/my-history")
    public ResponseEntity<ApiResponse<PagedResponse<AttemptHistoryDto>>> getStudentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUser UserPrincipal currentUser) {
        PagedResponse<AttemptHistoryDto> history = examAttemptService.getStudentAttemptHistory(currentUser.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(history, "Student examination history retrieved successfully"));
    }
}
