package com.smartassessment.dto.question;

import com.smartassessment.entity.QuestionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class QuestionRequestDto {

    private Long id;

    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    @NotBlank(message = "Question content is required")
    private String content;

    @NotNull(message = "Marks value is required")
    @DecimalMin(value = "0.5", message = "Marks must be at least 0.5")
    private Double marks;

    @DecimalMin(value = "0.0", message = "Negative marks cannot be negative")
    private Double negativeMarks;

    private String explanation;

    private List<OptionRequestDto> options;

    private CodingProblemDto codingProblem;

    public QuestionRequestDto() {
    }

    public QuestionRequestDto(Long id, QuestionType questionType, String content, Double marks, Double negativeMarks,
                              String explanation, List<OptionRequestDto> options, CodingProblemDto codingProblem) {
        this.id = id;
        this.questionType = questionType;
        this.content = content;
        this.marks = marks;
        this.negativeMarks = negativeMarks;
        this.explanation = explanation;
        this.options = options;
        this.codingProblem = codingProblem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<OptionRequestDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionRequestDto> options) {
        this.options = options;
    }

    public CodingProblemDto getCodingProblem() {
        return codingProblem;
    }

    public void setCodingProblem(CodingProblemDto codingProblem) {
        this.codingProblem = codingProblem;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private QuestionType questionType;
        private String content;
        private Double marks;
        private Double negativeMarks;
        private String explanation;
        private List<OptionRequestDto> options;
        private CodingProblemDto codingProblem;

        public Builder id(Long id) {
            this.id = id;
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

        public Builder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public Builder options(List<OptionRequestDto> options) {
            this.options = options;
            return this;
        }

        public Builder codingProblem(CodingProblemDto codingProblem) {
            this.codingProblem = codingProblem;
            return this;
        }

        public QuestionRequestDto build() {
            return new QuestionRequestDto(id, questionType, content, marks, negativeMarks, explanation, options, codingProblem);
        }
    }
}
