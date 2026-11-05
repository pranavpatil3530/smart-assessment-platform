package com.smartassessment.dto.attempt;

import com.smartassessment.entity.AttemptStatus;
import java.time.LocalDateTime;

public class AttemptHistoryDto {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private String categoryName;
    private Double score;
    private Double totalMarks;
    private Double percentage;
    private boolean passed;
    private AttemptStatus status;
    private LocalDateTime startTime;
    private LocalDateTime submittedTime;
    private Long timeTakenSeconds;

    public AttemptHistoryDto() {
    }

    public AttemptHistoryDto(Long attemptId, Long examId, String examTitle, String categoryName,
                             Double score, Double totalMarks, Double percentage, boolean passed,
                             AttemptStatus status, LocalDateTime startTime, LocalDateTime submittedTime,
                             Long timeTakenSeconds) {
        this.attemptId = attemptId;
        this.examId = examId;
        this.examTitle = examTitle;
        this.categoryName = categoryName;
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.passed = passed;
        this.status = status;
        this.startTime = startTime;
        this.submittedTime = submittedTime;
        this.timeTakenSeconds = timeTakenSeconds;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(LocalDateTime submittedTime) {
        this.submittedTime = submittedTime;
    }

    public Long getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public void setTimeTakenSeconds(Long timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long attemptId;
        private Long examId;
        private String examTitle;
        private String categoryName;
        private Double score;
        private Double totalMarks;
        private Double percentage;
        private boolean passed;
        private AttemptStatus status;
        private LocalDateTime startTime;
        private LocalDateTime submittedTime;
        private Long timeTakenSeconds;

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

        public Builder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public Builder score(Double score) {
            this.score = score;
            return this;
        }

        public Builder totalMarks(Double totalMarks) {
            this.totalMarks = totalMarks;
            return this;
        }

        public Builder percentage(Double percentage) {
            this.percentage = percentage;
            return this;
        }

        public Builder passed(boolean passed) {
            this.passed = passed;
            return this;
        }

        public Builder status(AttemptStatus status) {
            this.status = status;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder submittedTime(LocalDateTime submittedTime) {
            this.submittedTime = submittedTime;
            return this;
        }

        public Builder timeTakenSeconds(Long timeTakenSeconds) {
            this.timeTakenSeconds = timeTakenSeconds;
            return this;
        }

        public AttemptHistoryDto build() {
            return new AttemptHistoryDto(attemptId, examId, examTitle, categoryName, score,
                    totalMarks, percentage, passed, status, startTime, submittedTime, timeTakenSeconds);
        }
    }
}
