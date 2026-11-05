package com.smartassessment.dto.question;

public class OptionResponseDto {
    private Long id;
    private String optionText;
    private Integer optionOrder;
    private boolean correct;

    public OptionResponseDto() {
    }

    public OptionResponseDto(Long id, String optionText, Integer optionOrder, boolean correct) {
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

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String optionText;
        private Integer optionOrder;
        private boolean correct;

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

        public Builder correct(boolean correct) {
            this.correct = correct;
            return this;
        }

        public OptionResponseDto build() {
            return new OptionResponseDto(id, optionText, optionOrder, correct);
        }
    }
}
