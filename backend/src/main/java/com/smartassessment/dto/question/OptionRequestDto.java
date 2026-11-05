package com.smartassessment.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OptionRequestDto {
    private Long id;

    @NotBlank(message = "Option text is required")
    private String optionText;

    private Integer optionOrder;

    @NotNull(message = "Correctness flag must be specified")
    private Boolean correct;

    public OptionRequestDto() {
    }

    public OptionRequestDto(Long id, String optionText, Integer optionOrder, Boolean correct) {
        this.id = id;
        this.optionText = optionText;
        this.optionOrder = optionOrder;
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Integer getOptionOrder() {
        return optionOrder;
    }

    public void setOptionOrder(Integer optionOrder) {
        this.optionOrder = optionOrder;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String optionText;
        private Integer optionOrder;
        private Boolean correct;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder optionText(String optionText) {
            this.optionText = optionText;
            return this;
        }

        public Builder optionOrder(Integer optionOrder) {
            this.optionOrder = optionOrder;
            return this;
        }

        public Builder correct(Boolean correct) {
            this.correct = correct;
            return this;
        }

        public OptionRequestDto build() {
            return new OptionRequestDto(id, optionText, optionOrder, correct);
        }
    }
}
