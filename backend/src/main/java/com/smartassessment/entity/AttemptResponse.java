package com.smartassessment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "attempt_responses")
public class AttemptResponse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt examAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;

    @Column(name = "submitted_code", columnDefinition = "TEXT")
    private String submittedCode;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "marks_awarded")
    private Double marksAwarded = 0.0;

    public AttemptResponse() {
    }

    public AttemptResponse(Long id, ExamAttempt examAttempt, Question question,
                           QuestionOption selectedOption, String submittedCode,
                           Boolean isCorrect, Double marksAwarded) {
        this.id = id;
        this.examAttempt = examAttempt;
        this.question = question;
        this.selectedOption = selectedOption;
        this.submittedCode = submittedCode;
        this.isCorrect = isCorrect;
        this.marksAwarded = marksAwarded != null ? marksAwarded : 0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExamAttempt getExamAttempt() {
        return examAttempt;
    }

    public void setExamAttempt(ExamAttempt examAttempt) {
        this.examAttempt = examAttempt;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public QuestionOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(QuestionOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public String getSubmittedCode() {
        return submittedCode;
    }

    public void setSubmittedCode(String submittedCode) {
        this.submittedCode = submittedCode;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Double getMarksAwarded() {
        return marksAwarded;
    }

    public void setMarksAwarded(Double marksAwarded) {
        this.marksAwarded = marksAwarded;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private ExamAttempt examAttempt;
        private Question question;
        private QuestionOption selectedOption;
        private String submittedCode;
        private Boolean isCorrect;
        private Double marksAwarded = 0.0;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder examAttempt(ExamAttempt examAttempt) {
            this.examAttempt = examAttempt;
            return this;
        }

        public Builder question(Question question) {
            this.question = question;
            return this;
        }

        public Builder selectedOption(QuestionOption selectedOption) {
            this.selectedOption = selectedOption;
            return this;
        }

        public Builder submittedCode(String submittedCode) {
            this.submittedCode = submittedCode;
            return this;
        }

        public Builder isCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
            return this;
        }

        public Builder marksAwarded(Double marksAwarded) {
            this.marksAwarded = marksAwarded;
            return this;
        }

        public AttemptResponse build() {
            return new AttemptResponse(id, examAttempt, question, selectedOption, submittedCode, isCorrect, marksAwarded);
        }
    }
}
