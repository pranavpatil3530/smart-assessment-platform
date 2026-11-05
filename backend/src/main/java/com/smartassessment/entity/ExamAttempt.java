package com.smartassessment.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_attempts")
public class ExamAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "submitted_time")
    private LocalDateTime submittedTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    @Column(name = "score")
    private Double score = 0.0;

    @Column(name = "total_marks")
    private Double totalMarks = 0.0;

    @Column(name = "percentage")
    private Double percentage = 0.0;

    @Column(name = "is_passed")
    private Boolean passed = false;

    @Column(name = "total_correct")
    private Integer totalCorrect = 0;

    @Column(name = "total_wrong")
    private Integer totalWrong = 0;

    @Column(name = "total_unattempted")
    private Integer totalUnattempted = 0;

    @OneToMany(mappedBy = "examAttempt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AttemptResponse> responses = new ArrayList<>();

    public ExamAttempt() {
    }

    public ExamAttempt(Long id, User user, Exam exam, LocalDateTime startTime, LocalDateTime endTime,
                       LocalDateTime submittedTime, AttemptStatus status, Double score, Double totalMarks,
                       Double percentage, Boolean passed, Integer totalCorrect, Integer totalWrong,
                       Integer totalUnattempted, List<AttemptResponse> responses) {
        this.id = id;
        this.user = user;
        this.exam = exam;
        this.startTime = startTime;
        this.endTime = endTime;
        this.submittedTime = submittedTime;
        this.status = status != null ? status : AttemptStatus.IN_PROGRESS;
        this.score = score != null ? score : 0.0;
        this.totalMarks = totalMarks != null ? totalMarks : 0.0;
        this.percentage = percentage != null ? percentage : 0.0;
        this.passed = passed != null ? passed : false;
        this.totalCorrect = totalCorrect != null ? totalCorrect : 0;
        this.totalWrong = totalWrong != null ? totalWrong : 0;
        this.totalUnattempted = totalUnattempted != null ? totalUnattempted : 0;
        this.responses = responses != null ? responses : new ArrayList<>();
    }

    public void addResponse(AttemptResponse response) {
        responses.add(response);
        response.setExamAttempt(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
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

    public LocalDateTime getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(LocalDateTime submittedTime) {
        this.submittedTime = submittedTime;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
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

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
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

    public List<AttemptResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<AttemptResponse> responses) {
        this.responses = responses;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private User user;
        private Exam exam;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private LocalDateTime submittedTime;
        private AttemptStatus status = AttemptStatus.IN_PROGRESS;
        private Double score = 0.0;
        private Double totalMarks = 0.0;
        private Double percentage = 0.0;
        private Boolean passed = false;
        private Integer totalCorrect = 0;
        private Integer totalWrong = 0;
        private Integer totalUnattempted = 0;
        private List<AttemptResponse> responses = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder exam(Exam exam) {
            this.exam = exam;
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

        public Builder submittedTime(LocalDateTime submittedTime) {
            this.submittedTime = submittedTime;
            return this;
        }

        public Builder status(AttemptStatus status) {
            this.status = status;
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

        public Builder passed(Boolean passed) {
            this.passed = passed;
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

        public Builder responses(List<AttemptResponse> responses) {
            this.responses = responses;
            return this;
        }

        public ExamAttempt build() {
            return new ExamAttempt(id, user, exam, startTime, endTime, submittedTime, status, score,
                    totalMarks, percentage, passed, totalCorrect, totalWrong, totalUnattempted, responses);
        }
    }
}
