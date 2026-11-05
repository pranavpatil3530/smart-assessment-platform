package com.smartassessment.dto.attempt;

import com.smartassessment.dto.question.StudentQuestionDto;
import java.time.LocalDateTime;
import java.util.List;

public class StartExamResponseDto {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private Integer durationMinutes;
    private Double negativeMarkingRate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long remainingSeconds;
    private List<StudentQuestionDto> questions;

    public StartExamResponseDto() {
    }

    public StartExamResponseDto(Long attemptId, Long examId, String examTitle, Integer durationMinutes,
                                Double negativeMarkingRate, LocalDateTime startTime, LocalDateTime endTime,
                                Long remainingSeconds, List<StudentQuestionDto> questions) {
        this.attemptId = attemptId;
        this.examId = examId;
        this.examTitle = examTitle;
        this.durationMinutes = durationMinutes;
        this.negativeMarkingRate = negativeMarkingRate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.remainingSeconds = remainingSeconds;
        this.questions = questions;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getNegativeMarkingRate() {
        return negativeMarkingRate;
    }

    public void setNegativeMarkingRate(Double negativeMarkingRate) {
        this.negativeMarkingRate = negativeMarkingRate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(Long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public List<StudentQuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<StudentQuestionDto> questions) {
        this.questions = questions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long attemptId;
        private Long examId;
        private String examTitle;
        private Integer durationMinutes;
        private Double negativeMarkingRate;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Long remainingSeconds;
        private List<StudentQuestionDto> questions;

        public Builder attemptId(Long attemptId) {
            this.attemptId = attemptId;
            return this;
        }

        public Builder examId(Long examId) {
            this.examId = examId;
            return this;
        }

        public Builder examTitle(String examTitle) {
            this.examTitle = examTitle;
            return this;
        }

        public Builder durationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public Builder negativeMarkingRate(Double negativeMarkingRate) {
            this.negativeMarkingRate = negativeMarkingRate;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder remainingSeconds(Long remainingSeconds) {
            this.remainingSeconds = remainingSeconds;
            return this;
        }

        public Builder questions(List<StudentQuestionDto> questions) {
            this.questions = questions;
            return this;
        }

        public StartExamResponseDto build() {
            return new StartExamResponseDto(attemptId, examId, examTitle, durationMinutes,
                    negativeMarkingRate, startTime, endTime, remainingSeconds, questions);
        }
    }
}
