package com.smartassessment.dto.question;

public class StudentOptionDto {
    private Long id;
    private String optionText;
    private Integer optionOrder;

    public StudentOptionDto() {
    }

    public StudentOptionDto(Long id, String optionText, Integer optionOrder) {
        this.id = id;
        this.optionText = optionText;
        this.optionOrder = optionOrder;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String optionText;
        private Integer optionOrder;

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

        public StudentOptionDto build() {
            return new StudentOptionDto(id, optionText, optionOrder);
        }
    }
}
