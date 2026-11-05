package com.smartassessment.controller;

import com.smartassessment.dto.common.ApiResponse;
import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.dto.exam.ExamDetailResponseDto;
import com.smartassessment.dto.exam.ExamRequestDto;
import com.smartassessment.dto.exam.ExamResponseDto;
import com.smartassessment.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/api/exams/published")
    public ResponseEntity<ApiResponse<PagedResponse<ExamResponseDto>>> getPublishedExams(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PagedResponse<ExamResponseDto> exams = examService.getPublishedExams(categoryId, keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(exams, "Published examinations retrieved successfully"));
    }

    @GetMapping("/api/exams/{id}")
    public ResponseEntity<ApiResponse<ExamDetailResponseDto>> getExamDetails(@PathVariable Long id) {
        ExamDetailResponseDto exam = examService.getExamById(id);
        return ResponseEntity.ok(ApiResponse.success(exam, "Examination details retrieved successfully"));
    }

    @GetMapping("/api/admin/exams")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<ExamResponseDto>>> getAllExamsForAdmin(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PagedResponse<ExamResponseDto> exams = examService.getAllExams(categoryId, keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(exams, "Admin examination list retrieved successfully"));
    }

    @PostMapping("/api/admin/exams")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ExamResponseDto>> createExam(@Valid @RequestBody ExamRequestDto requestDto) {
        ExamResponseDto created = examService.createExam(requestDto);
        return new ResponseEntity<>(ApiResponse.success(created, "Examination created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/api/admin/exams/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ExamResponseDto>> updateExam(
            @PathVariable Long id,
            @Valid @RequestBody ExamRequestDto requestDto) {
        ExamResponseDto updated = examService.updateExam(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Examination updated successfully"));
    }

    @DeleteMapping("/api/admin/exams/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Examination deleted successfully"));
    }

    @PatchMapping("/api/admin/exams/{id}/publish")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ExamResponseDto>> togglePublishStatus(
            @PathVariable Long id,
            @RequestParam boolean published) {
        ExamResponseDto response = examService.togglePublish(id, published);
        return ResponseEntity.ok(ApiResponse.success(response, "Examination publish status updated to " + published));
    }
}
