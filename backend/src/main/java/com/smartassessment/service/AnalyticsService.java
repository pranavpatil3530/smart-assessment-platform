package com.smartassessment.service;

import com.smartassessment.dto.attempt.AttemptHistoryDto;
import com.smartassessment.dto.attempt.PerformanceAnalyticsDto;
import com.smartassessment.dto.common.PagedResponse;

public interface AnalyticsService {
    PerformanceAnalyticsDto getOverviewAnalytics();
    PagedResponse<AttemptHistoryDto> getExamAttempts(Long examId, int page, int size);
}
