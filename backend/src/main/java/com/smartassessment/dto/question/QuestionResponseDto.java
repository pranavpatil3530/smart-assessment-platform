package com.smartassessment.dto.question;

import com.smartassessment.entity.QuestionType;
import java.util.List;

public class QuestionResponseDto {
    private Long id;
    private Long examId;
    private QuestionType questionType;
    private String content;
    private Double marks;
    private Double negativeMarks;
    private String explanation;
    private List<OptionResponseDto> options;
    private CodingProblemDto codingProblem;

    public QuestionResponseDto() {
    }

    public QuestionResponseDto(Long id, Long examId, QuestionType questionType, String content,
                               Double marks, Double negativeMarks, String explanation,
                               List<OptionResponseDto> options, CodingProblemDto codingProblem) {
        this.id = id;
        this.examId = examId;
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

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
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

    public List<OptionResponseDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionResponseDto> options) {
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
        private Long examId;
        private QuestionType questionType;
        private String content;
        private Double marks;
        private Double negativeMarks;
        private String explanation;
        private List<OptionResponseDto> options;
        private CodingProblemDto codingProblem;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder examId(Long examId) {
            this.examId = examId;
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

        public Builder options(List<OptionResponseDto> options) {
            this.options = options;
            return this;
        }

        public Builder codingProblem(CodingProblemDto codingProblem) {
            this.codingProblem = codingProblem;
            return this;
        }

        public QuestionResponseDto build() {
            return new QuestionResponseDto(id, examId, questionType, content, marks, negativeMarks, explanation, options, codingProblem);
        }
    }
}
