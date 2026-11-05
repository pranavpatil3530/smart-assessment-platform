package com.smartassessment.dto.attempt;

import java.util.List;

public class SubmitExamRequestDto {
    private List<StudentAnswerSubmissionDto> answers;

    public SubmitExamRequestDto() {
    }

    public SubmitExamRequestDto(List<StudentAnswerSubmissionDto> answers) {
        this.answers = answers;
    }

    public List<StudentAnswerSubmissionDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<StudentAnswerSubmissionDto> answers) {
        this.answers = answers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<StudentAnswerSubmissionDto> answers;

        public Builder answers(List<StudentAnswerSubmissionDto> answers) {
            this.answers = answers;
            return this;
        }

        public SubmitExamRequestDto build() {
            return new SubmitExamRequestDto(answers);
        }
    }
}
