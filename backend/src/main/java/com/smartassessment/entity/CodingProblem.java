package com.smartassessment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "coding_problems")
public class CodingProblem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private Question question;

    @Column(name = "starter_code", columnDefinition = "TEXT")
    private String starterCode;

    @Column(nullable = false, length = 50)
    private String language = "Java";

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Column(name = "sample_input", columnDefinition = "TEXT")
    private String sampleInput;

    @Column(name = "sample_output", columnDefinition = "TEXT")
    private String sampleOutput;

    @Column(name = "test_cases_json", columnDefinition = "TEXT")
    private String testCasesJson;

    public CodingProblem() {
    }

    public CodingProblem(Long id, Question question, String starterCode, String language,
                         String constraints, String sampleInput, String sampleOutput, String testCasesJson) {
        this.id = id;
        this.question = question;
        this.starterCode = starterCode;
        this.language = language != null ? language : "Java";
        this.constraints = constraints;
        this.sampleInput = sampleInput;
        this.sampleOutput = sampleOutput;
        this.testCasesJson = testCasesJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getStarterCode() {
        return starterCode;
    }

    public void setStarterCode(String starterCode) {
        this.starterCode = starterCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public String getSampleInput() {
        return sampleInput;
    }

    public void setSampleInput(String sampleInput) {
        this.sampleInput = sampleInput;
    }

    public String getSampleOutput() {
        return sampleOutput;
    }

    public void setSampleOutput(String sampleOutput) {
        this.sampleOutput = sampleOutput;
    }

    public String getTestCasesJson() {
        return testCasesJson;
    }

    public void setTestCasesJson(String testCasesJson) {
        this.testCasesJson = testCasesJson;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Question question;
        private String starterCode;
        private String language = "Java";
        private String constraints;
        private String sampleInput;
        private String sampleOutput;
        private String testCasesJson;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder question(Question question) {
            this.question = question;
            return this;
        }

        public Builder starterCode(String starterCode) {
            this.starterCode = starterCode;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder constraints(String constraints) {
            this.constraints = constraints;
            return this;
        }

        public Builder sampleInput(String sampleInput) {
            this.sampleInput = sampleInput;
            return this;
        }

        public Builder sampleOutput(String sampleOutput) {
            this.sampleOutput = sampleOutput;
            return this;
        }

        public Builder testCasesJson(String testCasesJson) {
            this.testCasesJson = testCasesJson;
            return this;
        }

        public CodingProblem build() {
            return new CodingProblem(id, question, starterCode, language, constraints, sampleInput, sampleOutput, testCasesJson);
        }
    }
}
