package com.smartassessment.service.impl;

import com.smartassessment.dto.attempt.AttemptHistoryDto;
import com.smartassessment.dto.attempt.PerformanceAnalyticsDto;
import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.entity.ExamAttempt;
import com.smartassessment.entity.Role;
import com.smartassessment.repository.ExamAttemptRepository;
import com.smartassessment.repository.ExamRepository;
import com.smartassessment.repository.UserRepository;
import com.smartassessment.service.AnalyticsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final ExamAttemptRepository examAttemptRepository;

    public AnalyticsServiceImpl(ExamRepository examRepository,
                                UserRepository userRepository,
                                ExamAttemptRepository examAttemptRepository) {
        this.examRepository = examRepository;
        this.userRepository = userRepository;
        this.examAttemptRepository = examAttemptRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceAnalyticsDto getOverviewAnalytics() {
        long totalExams = examRepository.count();
        long publishedExams = examRepository.countByPublishedTrue();
        long totalStudents = userRepository.countByRole(Role.ROLE_STUDENT);
        long totalAttempts = examAttemptRepository.count();
        long passedAttempts = examAttemptRepository.countPassedAttempts();

        double passRate = totalAttempts > 0 ?
                Math.round(((double) passedAttempts / totalAttempts * 100.0) * 100.0) / 100.0 : 0.0;

        Double avgPercentage = examAttemptRepository.calculateOverallAveragePercentage();
        double averageScore = avgPercentage != null ? Math.round(avgPercentage * 100.0) / 100.0 : 0.0;

        Pageable topFive = PageRequest.of(0, 5, Sort.by("startTime").descending());
        List<AttemptHistoryDto> recent = examAttemptRepository.findAll(topFive).getContent().stream()
                .map(this::mapToHistoryDto)
                .collect(Collectors.toList());

        return PerformanceAnalyticsDto.builder()
                .totalExams(totalExams)
                .totalPublishedExams(publishedExams)
                .totalStudents(totalStudents)
                .totalAttempts(totalAttempts)
                .passedAttempts(passedAttempts)
                .overallPassRate(passRate)
                .overallAverageScore(averageScore)
                .recentAttempts(recent)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AttemptHistoryDto> getExamAttempts(Long examId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExamAttempt> attemptsPage = examAttemptRepository.findByExamIdOrderByStartTimeDesc(examId, pageable);

        List<AttemptHistoryDto> content = attemptsPage.getContent().stream()
                .map(this::mapToHistoryDto)
                .collect(Collectors.toList());

        return PagedResponse.<AttemptHistoryDto>builder()
                .content(content)
                .pageNumber(attemptsPage.getNumber())
                .pageSize(attemptsPage.getSize())
                .totalElements(attemptsPage.getTotalElements())
                .totalPages(attemptsPage.getTotalPages())
                .last(attemptsPage.isLast())
                .build();
    }

    private AttemptHistoryDto mapToHistoryDto(ExamAttempt att) {
        long duration = 0L;
        if (att.getSubmittedTime() != null && att.getStartTime() != null) {
            duration = Duration.between(att.getStartTime(), att.getSubmittedTime()).getSeconds();
        }
        return AttemptHistoryDto.builder()
                .attemptId(att.getId())
                .examId(att.getExam().getId())
                .examTitle(att.getExam().getTitle())
                .categoryName(att.getExam().getCategory().getName())
                .score(att.getScore())
                .totalMarks(att.getTotalMarks())
                .percentage(att.getPercentage())
                .passed(Boolean.TRUE.equals(att.getPassed()))
                .status(att.getStatus())
                .startTime(att.getStartTime())
                .submittedTime(att.getSubmittedTime())
                .timeTakenSeconds(duration)
                .build();
    }
}
