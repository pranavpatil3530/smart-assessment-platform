package com.smartassessment.controller;

import com.smartassessment.dto.attempt.AttemptHistoryDto;
import com.smartassessment.dto.attempt.PerformanceAnalyticsDto;
import com.smartassessment.dto.common.ApiResponse;
import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;

    public AdminAnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<PerformanceAnalyticsDto>> getOverviewAnalytics() {
        PerformanceAnalyticsDto analytics = analyticsService.getOverviewAnalytics();
        return ResponseEntity.ok(ApiResponse.success(analytics, "Admin analytics overview retrieved successfully"));
    }

    @GetMapping("/exams/{examId}/attempts")
    public ResponseEntity<ApiResponse<PagedResponse<AttemptHistoryDto>>> getExamAttempts(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<AttemptHistoryDto> attempts = analyticsService.getExamAttempts(examId, page, size);
        return ResponseEntity.ok(ApiResponse.success(attempts, "Exam attempts analytics retrieved successfully"));
    }
}
