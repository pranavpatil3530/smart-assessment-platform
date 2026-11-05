package com.smartassessment.dto.attempt;

import com.smartassessment.dto.question.OptionResponseDto;
import com.smartassessment.entity.QuestionType;
import java.util.List;

public class QuestionResultDto {
    private Long questionId;
    private QuestionType questionType;
    private String content;
    private Double marks;
    private Double negativeMarks;
    private Long selectedOptionId;
    private String selectedOptionText;
    private Long correctOptionId;
    private String correctOptionText;
    private String submittedCode;
    private Boolean isCorrect;
    private Double marksAwarded;
    private String explanation;
    private List<OptionResponseDto> options;

    public QuestionResultDto() {
    }

    public QuestionResultDto(Long questionId, QuestionType questionType, String content, Double marks,
                             Double negativeMarks, Long selectedOptionId, String selectedOptionText,
                             Long correctOptionId, String correctOptionText, String submittedCode,
                             Boolean isCorrect, Double marksAwarded, String explanation,
                             List<OptionResponseDto> options) {
        this.questionId = questionId;
        this.questionType = questionType;
        this.content = content;
        this.marks = marks;
        this.negativeMarks = negativeMarks;
        this.selectedOptionId = selectedOptionId;
        this.selectedOptionText = selectedOptionText;
        this.correctOptionId = correctOptionId;
        this.correctOptionText = correctOptionText;
        this.submittedCode = submittedCode;
        this.isCorrect = isCorrect;
        this.marksAwarded = marksAwarded;
        this.explanation = explanation;
        this.options = options;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
    }

    public Double getNegativeMarks() {
        return negativeMarks;
    }

    public void setNegativeMarks(Double negativeMarks) {
        this.negativeMarks = negativeMarks;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public String getSelectedOptionText() {
        return selectedOptionText;
    }

    public void setSelectedOptionText(String selectedOptionText) {
        this.selectedOptionText = selectedOptionText;
    }

    public Long getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Long correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    public String getCorrectOptionText() {
        return correctOptionText;
    }

    public void setCorrectOptionText(String correctOptionText) {
        this.correctOptionText = correctOptionText;
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

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<OptionResponseDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionResponseDto> options) {
        this.options = options;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long questionId;
        private QuestionType questionType;
        private String content;
        private Double marks;
        private Double negativeMarks;
        private Long selectedOptionId;
        private String selectedOptionText;
        private Long correctOptionId;
        private String correctOptionText;
        private String submittedCode;
        private Boolean isCorrect;
        private Double marksAwarded;
        private String explanation;
        private List<OptionResponseDto> options;

        public Builder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public Builder questionType(QuestionType questionType) {
            this.questionType = questionType;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder marks(Double marks) {
            this.marks = marks;
            return this;
        }

        public Builder negativeMarks(Double negativeMarks) {
            this.negativeMarks = negativeMarks;
            return this;
        }

        public Builder selectedOptionId(Long selectedOptionId) {
            this.selectedOptionId = selectedOptionId;
            return this;
        }

        public Builder selectedOptionText(String selectedOptionText) {
            this.selectedOptionText = selectedOptionText;
            return this;
        }

        public Builder correctOptionId(Long correctOptionId) {
            this.correctOptionId = correctOptionId;
            return this;
        }

        public Builder correctOptionText(String correctOptionText) {
            this.correctOptionText = correctOptionText;
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

        public Builder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public Builder options(List<OptionResponseDto> options) {
            this.options = options;
            return this;
        }

        public QuestionResultDto build() {
            return new QuestionResultDto(questionId, questionType, content, marks, negativeMarks,
                    selectedOptionId, selectedOptionText, correctOptionId, correctOptionText,
                    submittedCode, isCorrect, marksAwarded, explanation, options);
        }
    }
}
