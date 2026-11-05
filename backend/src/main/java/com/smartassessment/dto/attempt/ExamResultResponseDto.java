package com.smartassessment.dto.attempt;

import com.smartassessment.entity.AttemptStatus;
import java.time.LocalDateTime;
import java.util.List;

public class ExamResultResponseDto {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private String studentName;
    private String studentEmail;
    private Double score;
    private Double totalMarks;
    private Integer passingMarks;
    private Double percentage;
    private boolean passed;
    private Integer totalQuestions;
    private Integer totalAttempted;
    private Integer totalCorrect;
    private Integer totalWrong;
    private Integer totalUnattempted;
    private Double accuracyRate;
    private AttemptStatus status;
    private LocalDateTime startTime;
    private LocalDateTime submittedTime;
    private Long timeTakenSeconds;
    private List<QuestionResultDto> questionBreakdown;

    public ExamResultResponseDto() {
    }

    public ExamResultResponseDto(Long attemptId, Long examId, String examTitle, String studentName, String studentEmail,
                                 Double score, Double totalMarks, Integer passingMarks, Double percentage, boolean passed,
                                 Integer totalQuestions, Integer totalAttempted, Integer totalCorrect, Integer totalWrong,
                                 Integer totalUnattempted, Double accuracyRate, AttemptStatus status,
                                 LocalDateTime startTime, LocalDateTime submittedTime, Long timeTakenSeconds,
                                 List<QuestionResultDto> questionBreakdown) {
        this.attemptId = attemptId;
        this.examId = examId;
        this.examTitle = examTitle;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.score = score;
        this.totalMarks = totalMarks;
        this.passingMarks = passingMarks;
        this.percentage = percentage;
        this.passed = passed;
        this.totalQuestions = totalQuestions;
        this.totalAttempted = totalAttempted;
        this.totalCorrect = totalCorrect;
        this.totalWrong = totalWrong;
        this.totalUnattempted = totalUnattempted;
        this.accuracyRate = accuracyRate;
        this.status = status;
        this.startTime = startTime;
        this.submittedTime = submittedTime;
        this.timeTakenSeconds = timeTakenSeconds;
        this.questionBreakdown = questionBreakdown;
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
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

    public Integer getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(Integer passingMarks) {
        this.passingMarks = passingMarks;
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

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getTotalAttempted() {
        return totalAttempted;
    }

    public void setTotalAttempted(Integer totalAttempted) {
        this.totalAttempted = totalAttempted;
    }

    public Integer getTotalCorrect() {
        return totalCorrect;
    }

    public void setTotalCorrect(Integer totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public Integer getTotalWrong() {
        return totalWrong;
    }

    public void setTotalWrong(Integer totalWrong) {
        this.totalWrong = totalWrong;
    }

    public Integer getTotalUnattempted() {
        return totalUnattempted;
    }

    public void setTotalUnattempted(Integer totalUnattempted) {
        this.totalUnattempted = totalUnattempted;
    }

    public Double getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(Double accuracyRate) {
        this.accuracyRate = accuracyRate;
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

    public List<QuestionResultDto> getQuestionBreakdown() {
        return questionBreakdown;
    }

    public void setQuestionBreakdown(List<QuestionResultDto> questionBreakdown) {
        this.questionBreakdown = questionBreakdown;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long attemptId;
        private Long examId;
        private String examTitle;
        private String studentName;
        private String studentEmail;
        private Double score;
        private Double totalMarks;
        private Integer passingMarks;
        private Double percentage;
        private boolean passed;
        private Integer totalQuestions;
        private Integer totalAttempted;
        private Integer totalCorrect;
        private Integer totalWrong;
        private Integer totalUnattempted;
        private Double accuracyRate;
        private AttemptStatus status;
        private LocalDateTime startTime;
        private LocalDateTime submittedTime;
        private Long timeTakenSeconds;
        private List<QuestionResultDto> questionBreakdown;

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

        public Builder studentName(String studentName) {
            this.studentName = studentName;
            return this;
        }

        public Builder studentEmail(String studentEmail) {
            this.studentEmail = studentEmail;
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

        public Builder passingMarks(Integer passingMarks) {
            this.passingMarks = passingMarks;
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

        public Builder totalQuestions(Integer totalQuestions) {
            this.totalQuestions = totalQuestions;
            return this;
        }

        public Builder totalAttempted(Integer totalAttempted) {
            this.totalAttempted = totalAttempted;
            return this;
        }

        public Builder totalCorrect(Integer totalCorrect) {
            this.totalCorrect = totalCorrect;
            return this;
        }

        public Builder totalWrong(Integer totalWrong) {
            this.totalWrong = totalWrong;
            return this;
        }

        public Builder totalUnattempted(Integer totalUnattempted) {
            this.totalUnattempted = totalUnattempted;
            return this;
        }

        public Builder accuracyRate(Double accuracyRate) {
            this.accuracyRate = accuracyRate;
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

        public Builder questionBreakdown(List<QuestionResultDto> questionBreakdown) {
            this.questionBreakdown = questionBreakdown;
            return this;
        }

        public ExamResultResponseDto build() {
            return new ExamResultResponseDto(attemptId, examId, examTitle, studentName, studentEmail,
                    score, totalMarks, passingMarks, percentage, passed, totalQuestions, totalAttempted,
                    totalCorrect, totalWrong, totalUnattempted, accuracyRate, status, startTime,
                    submittedTime, timeTakenSeconds, questionBreakdown);
        }
    }
}
