package com.smartassessment.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 20)
    private QuestionType questionType = QuestionType.MCQ;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Double marks = 1.0;

    @Column(name = "negative_marks", nullable = false)
    private Double negativeMarks = 0.25;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("optionOrder ASC")
    private List<QuestionOption> options = new ArrayList<>();

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CodingProblem codingProblem;

    public Question() {
    }

    public Question(Long id, Exam exam, QuestionType questionType, String content, Double marks,
                    Double negativeMarks, String explanation, List<QuestionOption> options,
                    CodingProblem codingProblem) {
        this.id = id;
        this.exam = exam;
        this.questionType = questionType != null ? questionType : QuestionType.MCQ;
        this.content = content;
        this.marks = marks != null ? marks : 1.0;
        this.negativeMarks = negativeMarks != null ? negativeMarks : 0.25;
        this.explanation = explanation;
        this.options = options != null ? options : new ArrayList<>();
        this.codingProblem = codingProblem;
    }

    public void addOption(QuestionOption option) {
        options.add(option);
        option.setQuestion(this);
    }

    public void removeOption(QuestionOption option) {
        options.remove(option);
        option.setQuestion(null);
    }

    public void setCodingProblem(CodingProblem problem) {
        this.codingProblem = problem;
        if (problem != null) {
            problem.setQuestion(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
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

    public List<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }

    public CodingProblem getCodingProblem() {
        return codingProblem;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Exam exam;
        private QuestionType questionType = QuestionType.MCQ;
        private String content;
        private Double marks = 1.0;
        private Double negativeMarks = 0.25;
        private String explanation;
        private List<QuestionOption> options = new ArrayList<>();
        private CodingProblem codingProblem;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder exam(Exam exam) {
            this.exam = exam;
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

        public Builder options(List<QuestionOption> options) {
            this.options = options;
            return this;
        }

        public Builder codingProblem(CodingProblem codingProblem) {
            this.codingProblem = codingProblem;
            return this;
        }

        public Question build() {
            return new Question(id, exam, questionType, content, marks, negativeMarks, explanation, options, codingProblem);
        }
    }
}
