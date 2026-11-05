package com.smartassessment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question_options")
public class QuestionOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    private String optionText;

    @Column(name = "option_order", nullable = false)
    private Integer optionOrder;

    @Column(name = "is_correct", nullable = false)
    private boolean correct = false;

    public QuestionOption() {
    }

    public QuestionOption(Long id, Question question, String optionText, Integer optionOrder, boolean correct) {
        this.id = id;
        this.question = question;
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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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
        private Question question;
        private String optionText;
        private Integer optionOrder;
        private boolean correct = false;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder question(Question question) {
            this.question = question;
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

        public QuestionOption build() {
            return new QuestionOption(id, question, optionText, optionOrder, correct);
        }
    }
}
