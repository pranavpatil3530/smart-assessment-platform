package com.smartassessment.dto.exam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDto {
    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Long examCount;

    public CategoryDto() {
    }

    public CategoryDto(Long id, String name, String description, Long examCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.examCount = examCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getExamCount() {
        return examCount;
    }

    public void setExamCount(Long examCount) {
        this.examCount = examCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private Long examCount;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder examCount(Long examCount) {
            this.examCount = examCount;
            return this;
        }

        public CategoryDto build() {
            return new CategoryDto(id, name, description, examCount);
        }
    }
}
