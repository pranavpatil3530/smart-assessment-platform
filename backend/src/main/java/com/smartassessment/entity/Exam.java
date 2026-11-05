package com.smartassessment.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exams")
public class Exam extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "passing_marks", nullable = false)
    private Integer passingMarks;

    @Column(name = "negative_marking_rate", nullable = false)
    private Double negativeMarkingRate = 0.25;

    @Column(name = "max_attempts", nullable = false)
    private Integer maxAttempts = 3;

    @Column(name = "is_published", nullable = false)
    private boolean published = false;

    @Column(name = "randomize_questions", nullable = false)
    private boolean randomizeQuestions = true;

    @Column(name = "randomize_options", nullable = false)
    private boolean randomizeOptions = true;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamAttempt> attempts = new ArrayList<>();

    public Exam() {
    }

    public Exam(Long id, String title, String description, Category category, Integer durationMinutes,
                Integer totalMarks, Integer passingMarks, Double negativeMarkingRate, Integer maxAttempts,
                boolean published, boolean randomizeQuestions, boolean randomizeOptions,
                List<Question> questions, List<ExamAttempt> attempts) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.durationMinutes = durationMinutes;
        this.totalMarks = totalMarks;
        this.passingMarks = passingMarks;
        this.negativeMarkingRate = negativeMarkingRate != null ? negativeMarkingRate : 0.25;
        this.maxAttempts = maxAttempts != null ? maxAttempts : 3;
        this.published = published;
        this.randomizeQuestions = randomizeQuestions;
        this.randomizeOptions = randomizeOptions;
        this.questions = questions != null ? questions : new ArrayList<>();
        this.attempts = attempts != null ? attempts : new ArrayList<>();
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setExam(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setExam(null);
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<ExamAttempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<ExamAttempt> attempts) {
        this.attempts = attempts;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private Category category;
        private Integer durationMinutes;
        private Integer totalMarks;
        private Integer passingMarks;
        private Double negativeMarkingRate = 0.25;
        private Integer maxAttempts = 3;
        private boolean published = false;
        private boolean randomizeQuestions = true;
        private boolean randomizeOptions = true;
        private List<Question> questions = new ArrayList<>();
        private List<ExamAttempt> attempts = new ArrayList<>();

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

        public Builder category(Category category) {
            this.category = category;
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

        public Builder questions(List<Question> questions) {
            this.questions = questions;
            return this;
        }

        public Builder attempts(List<ExamAttempt> attempts) {
            this.attempts = attempts;
            return this;
        }

        public Exam build() {
            return new Exam(id, title, description, category, durationMinutes, totalMarks,
                    passingMarks, negativeMarkingRate, maxAttempts, published,
                    randomizeQuestions, randomizeOptions, questions, attempts);
        }
    }
}
