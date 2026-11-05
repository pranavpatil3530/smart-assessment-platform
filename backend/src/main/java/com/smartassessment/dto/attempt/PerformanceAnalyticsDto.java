package com.smartassessment.dto.attempt;

import java.util.List;

public class PerformanceAnalyticsDto {
    private long totalExams;
    private long totalPublishedExams;
    private long totalStudents;
    private long totalAttempts;
    private long passedAttempts;
    private double overallPassRate;
    private double overallAverageScore;
    private List<AttemptHistoryDto> recentAttempts;

    public PerformanceAnalyticsDto() {
    }

    public PerformanceAnalyticsDto(long totalExams, long totalPublishedExams, long totalStudents, long totalAttempts,
                                   long passedAttempts, double overallPassRate, double overallAverageScore,
                                   List<AttemptHistoryDto> recentAttempts) {
        this.totalExams = totalExams;
        this.totalPublishedExams = totalPublishedExams;
        this.totalStudents = totalStudents;
        this.totalAttempts = totalAttempts;
        this.passedAttempts = passedAttempts;
        this.overallPassRate = overallPassRate;
        this.overallAverageScore = overallAverageScore;
        this.recentAttempts = recentAttempts;
    }

    public long getTotalExams() {
        return totalExams;
    }

    public void setTotalExams(long totalExams) {
        this.totalExams = totalExams;
    }

    public long getTotalPublishedExams() {
        return totalPublishedExams;
    }

    public void setTotalPublishedExams(long totalPublishedExams) {
        this.totalPublishedExams = totalPublishedExams;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(long totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public long getPassedAttempts() {
        return passedAttempts;
    }

    public void setPassedAttempts(long passedAttempts) {
        this.passedAttempts = passedAttempts;
    }

    public double getOverallPassRate() {
        return overallPassRate;
    }

    public void setOverallPassRate(double overallPassRate) {
        this.overallPassRate = overallPassRate;
    }

    public double getOverallAverageScore() {
        return overallAverageScore;
    }

    public void setOverallAverageScore(double overallAverageScore) {
        this.overallAverageScore = overallAverageScore;
    }

    public List<AttemptHistoryDto> getRecentAttempts() {
        return recentAttempts;
    }

    public void setRecentAttempts(List<AttemptHistoryDto> recentAttempts) {
        this.recentAttempts = recentAttempts;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long totalExams;
        private long totalPublishedExams;
        private long totalStudents;
        private long totalAttempts;
        private long passedAttempts;
        private double overallPassRate;
        private double overallAverageScore;
        private List<AttemptHistoryDto> recentAttempts;

        public Builder totalExams(long totalExams) {
            this.totalExams = totalExams;
            return this;
        }

        public Builder totalPublishedExams(long totalPublishedExams) {
            this.totalPublishedExams = totalPublishedExams;
            return this;
        }

        public Builder totalStudents(long totalStudents) {
            this.totalStudents = totalStudents;
            return this;
        }

        public Builder totalAttempts(long totalAttempts) {
            this.totalAttempts = totalAttempts;
            return this;
        }

        public Builder passedAttempts(long passedAttempts) {
            this.passedAttempts = passedAttempts;
            return this;
        }

        public Builder overallPassRate(double overallPassRate) {
            this.overallPassRate = overallPassRate;
            return this;
        }

        public Builder overallAverageScore(double overallAverageScore) {
            this.overallAverageScore = overallAverageScore;
            return this;
        }

        public Builder recentAttempts(List<AttemptHistoryDto> recentAttempts) {
            this.recentAttempts = recentAttempts;
            return this;
        }

        public PerformanceAnalyticsDto build() {
            return new PerformanceAnalyticsDto(totalExams, totalPublishedExams, totalStudents,
                    totalAttempts, passedAttempts, overallPassRate, overallAverageScore, recentAttempts);
        }
    }
}
