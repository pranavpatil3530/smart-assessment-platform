package com.smartassessment.dto.question;

public class CodingProblemDto {
    private Long id;
    private String starterCode;
    private String language;
    private String constraints;
    private String sampleInput;
    private String sampleOutput;
    private String testCasesJson;

    public CodingProblemDto() {
    }

    public CodingProblemDto(Long id, String starterCode, String language, String constraints, String sampleInput, String sampleOutput, String testCasesJson) {
        this.id = id;
        this.starterCode = starterCode;
        this.language = language;
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
        private String starterCode;
        private String language;
        private String constraints;
        private String sampleInput;
        private String sampleOutput;
        private String testCasesJson;

        public Builder id(Long id) {
            this.id = id;
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

        public CodingProblemDto build() {
            return new CodingProblemDto(id, starterCode, language, constraints, sampleInput, sampleOutput, testCasesJson);
        }
    }
}
