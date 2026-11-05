package com.smartassessment.dto.exam;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExamRequestDto {

    @NotBlank(message = "Exam title is required")
    private String title;

    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Duration in minutes is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotNull(message = "Total marks is required")
    @Min(value = 1, message = "Total marks must be at least 1")
    private Integer totalMarks;

    @NotNull(message = "Passing marks is required")
    @Min(value = 1, message = "Passing marks must be at least 1")
    private Integer passingMarks;

    @DecimalMin(value = "0.0", message = "Negative marking rate cannot be negative")
    private Double negativeMarkingRate;

    @Min(value = 1, message = "Max attempts must be at least 1")
    private Integer maxAttempts;

    private Boolean published;
    private Boolean randomizeQuestions;
    private Boolean randomizeOptions;

    public ExamRequestDto() {
    }

    public ExamRequestDto(String title, String description, Long categoryId, Integer durationMinutes,
                          Integer totalMarks, Integer passingMarks, Double negativeMarkingRate,
                          Integer maxAttempts, Boolean published, Boolean randomizeQuestions, Boolean randomizeOptions) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.durationMinutes = durationMinutes;
        this.totalMarks = totalMarks;
        this.passingMarks = passingMarks;
        this.negativeMarkingRate = negativeMarkingRate;
        this.maxAttempts = maxAttempts;
        this.published = published;
        this.randomizeQuestions = randomizeQuestions;
        this.randomizeOptions = randomizeOptions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Integer getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(Integer passingMarks) {
        this.passingMarks = passingMarks;
    }

    public Double getNegativeMarkingRate() {
        return negativeMarkingRate;
    }

    public void setNegativeMarkingRate(Double negativeMarkingRate) {
        this.negativeMarkingRate = negativeMarkingRate;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getRandomizeQuestions() {
        return randomizeQuestions;
    }

    public void setRandomizeQuestions(Boolean randomizeQuestions) {
        this.randomizeQuestions = randomizeQuestions;
    }

    public Boolean getRandomizeOptions() {
        return randomizeOptions;
    }

    public void setRandomizeOptions(Boolean randomizeOptions) {
        this.randomizeOptions = randomizeOptions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String description;
        private Long categoryId;
        private Integer durationMinutes;
        private Integer totalMarks;
        private Integer passingMarks;
        private Double negativeMarkingRate;
        private Integer maxAttempts;
        private Boolean published;
        private Boolean randomizeQuestions;
        private Boolean randomizeOptions;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder durationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public Builder totalMarks(Integer totalMarks) {
            this.totalMarks = totalMarks;
            return this;
        }

        public Builder passingMarks(Integer passingMarks) {
            this.passingMarks = passingMarks;
            return this;
        }

        public Builder negativeMarkingRate(Double negativeMarkingRate) {
            this.negativeMarkingRate = negativeMarkingRate;
            return this;
        }

        public Builder maxAttempts(Integer maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public Builder published(Boolean published) {
            this.published = published;
            return this;
        }

        public Builder randomizeQuestions(Boolean randomizeQuestions) {
            this.randomizeQuestions = randomizeQuestions;
            return this;
        }

        public Builder randomizeOptions(Boolean randomizeOptions) {
            this.randomizeOptions = randomizeOptions;
            return this;
        }

        public ExamRequestDto build() {
            return new ExamRequestDto(title, description, categoryId, durationMinutes, totalMarks,
                    passingMarks, negativeMarkingRate, maxAttempts, published, randomizeQuestions, randomizeOptions);
        }
    }
}
