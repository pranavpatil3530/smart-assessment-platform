package com.smartassessment.dto.exam;

import com.smartassessment.dto.question.QuestionResponseDto;
import java.time.LocalDateTime;
import java.util.List;

public class ExamDetailResponseDto {
    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Integer durationMinutes;
    private Integer totalMarks;
    private Integer passingMarks;
    private Double negativeMarkingRate;
    private Integer maxAttempts;
    private boolean published;
    private boolean randomizeQuestions;
    private boolean randomizeOptions;
    private int totalQuestions;
    private LocalDateTime createdAt;
    private List<QuestionResponseDto> questions;
    private Long instituteId;
    private String instituteName;
    private boolean assignAllStudents = true;
    private int assignedCandidatesCount;

    public ExamDetailResponseDto() {
    }

    public ExamDetailResponseDto(Long id, String title, String description, Long categoryId, String categoryName,
                                 Integer durationMinutes, Integer totalMarks, Integer passingMarks,
                                 Double negativeMarkingRate, Integer maxAttempts, boolean published,
                                 boolean randomizeQuestions, boolean randomizeOptions, int totalQuestions,
                                 LocalDateTime createdAt, List<QuestionResponseDto> questions) {
        this(id, title, description, categoryId, categoryName, durationMinutes, totalMarks, passingMarks,
             negativeMarkingRate, maxAttempts, published, randomizeQuestions, randomizeOptions, totalQuestions,
             createdAt, questions, null, null, true, 0);
    }

    public ExamDetailResponseDto(Long id, String title, String description, Long categoryId, String categoryName,
                                 Integer durationMinutes, Integer totalMarks, Integer passingMarks,
                                 Double negativeMarkingRate, Integer maxAttempts, boolean published,
                                 boolean randomizeQuestions, boolean randomizeOptions, int totalQuestions,
                                 LocalDateTime createdAt, List<QuestionResponseDto> questions,
                                 Long instituteId, String instituteName, boolean assignAllStudents,
                                 int assignedCandidatesCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.durationMinutes = durationMinutes;
        this.totalMarks = totalMarks;
        this.passingMarks = passingMarks;
        this.negativeMarkingRate = negativeMarkingRate;
        this.maxAttempts = maxAttempts;
        this.published = published;
        this.randomizeQuestions = randomizeQuestions;
        this.randomizeOptions = randomizeOptions;
        this.totalQuestions = totalQuestions;
        this.createdAt = createdAt;
        this.questions = questions;
        this.instituteId = instituteId;
        this.instituteName = instituteName;
        this.assignAllStudents = assignAllStudents;
        this.assignedCandidatesCount = assignedCandidatesCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isRandomizeQuestions() {
        return randomizeQuestions;
    }

    public void setRandomizeQuestions(boolean randomizeQuestions) {
        this.randomizeQuestions = randomizeQuestions;
    }

    public boolean isRandomizeOptions() {
        return randomizeOptions;
    }

    public void setRandomizeOptions(boolean randomizeOptions) {
        this.randomizeOptions = randomizeOptions;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<QuestionResponseDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponseDto> questions) {
        this.questions = questions;
    }

    public Long getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(Long instituteId) {
        this.instituteId = instituteId;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public boolean isAssignAllStudents() {
        return assignAllStudents;
    }

    public void setAssignAllStudents(boolean assignAllStudents) {
        this.assignAllStudents = assignAllStudents;
    }

    public int getAssignedCandidatesCount() {
        return assignedCandidatesCount;
    }

    public void setAssignedCandidatesCount(int assignedCandidatesCount) {
        this.assignedCandidatesCount = assignedCandidatesCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private Long categoryId;
        private String categoryName;
        private Integer durationMinutes;
        private Integer totalMarks;
        private Integer passingMarks;
        private Double negativeMarkingRate;
        private Integer maxAttempts;
        private boolean published;
        private boolean randomizeQuestions;
        private boolean randomizeOptions;
        private int totalQuestions;
        private LocalDateTime createdAt;
        private List<QuestionResponseDto> questions;
        private Long instituteId;
        private String instituteName;
        private boolean assignAllStudents = true;
        private int assignedCandidatesCount;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

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

        public Builder categoryName(String categoryName) {
            this.categoryName = categoryName;
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

        public Builder published(boolean published) {
            this.published = published;
            return this;
        }

        public Builder randomizeQuestions(boolean randomizeQuestions) {
            this.randomizeQuestions = randomizeQuestions;
            return this;
        }

        public Builder randomizeOptions(boolean randomizeOptions) {
            this.randomizeOptions = randomizeOptions;
            return this;
        }

        public Builder totalQuestions(int totalQuestions) {
            this.totalQuestions = totalQuestions;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder questions(List<QuestionResponseDto> questions) {
            this.questions = questions;
            return this;
        }

        public Builder instituteId(Long instituteId) {
            this.instituteId = instituteId;
            return this;
        }

        public Builder instituteName(String instituteName) {
            this.instituteName = instituteName;
            return this;
        }

        public Builder assignAllStudents(boolean assignAllStudents) {
            this.assignAllStudents = assignAllStudents;
            return this;
        }

        public Builder assignedCandidatesCount(int assignedCandidatesCount) {
            this.assignedCandidatesCount = assignedCandidatesCount;
            return this;
        }

        public ExamDetailResponseDto build() {
            return new ExamDetailResponseDto(id, title, description, categoryId, categoryName,
                    durationMinutes, totalMarks, passingMarks, negativeMarkingRate, maxAttempts,
                    published, randomizeQuestions, randomizeOptions, totalQuestions, createdAt, questions,
                    instituteId, instituteName, assignAllStudents, assignedCandidatesCount);
        }
    }
}
