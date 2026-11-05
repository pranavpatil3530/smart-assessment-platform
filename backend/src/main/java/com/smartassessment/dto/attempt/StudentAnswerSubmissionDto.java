package com.smartassessment.dto.attempt;

public class StudentAnswerSubmissionDto {
    private Long questionId;
    private Long selectedOptionId;
    private String submittedCode;

    public StudentAnswerSubmissionDto() {
    }

    public StudentAnswerSubmissionDto(Long questionId, Long selectedOptionId, String submittedCode) {
        this.questionId = questionId;
        this.selectedOptionId = selectedOptionId;
        this.submittedCode = submittedCode;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public String getSubmittedCode() {
        return submittedCode;
    }

    public void setSubmittedCode(String submittedCode) {
        this.submittedCode = submittedCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long questionId;
        private Long selectedOptionId;
        private String submittedCode;

        public Builder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public Builder selectedOptionId(Long selectedOptionId) {
            this.selectedOptionId = selectedOptionId;
            return this;
        }

        public Builder submittedCode(String submittedCode) {
            this.submittedCode = submittedCode;
            return this;
        }

        public StudentAnswerSubmissionDto build() {
            return new StudentAnswerSubmissionDto(questionId, selectedOptionId, submittedCode);
        }
    }
}
