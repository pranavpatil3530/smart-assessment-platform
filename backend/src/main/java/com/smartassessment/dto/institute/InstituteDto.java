package com.smartassessment.dto.institute;

import com.smartassessment.entity.ApprovalStatus;

import java.time.LocalDateTime;

public class InstituteDto {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private ApprovalStatus status;
    private String rejectionReason;
    private Long adminUserId;
    private String adminUsername;
    private String adminFullName;
    private int studentCount;
    private int examCount;
    private LocalDateTime createdAt;

    public InstituteDto() {
    }

    public InstituteDto(Long id, String name, String code, String description, String contactEmail,
                        String contactPhone, ApprovalStatus status, String rejectionReason,
                        Long adminUserId, String adminUsername, String adminFullName,
                        int studentCount, int examCount, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.adminUserId = adminUserId;
        this.adminUsername = adminUsername;
        this.adminFullName = adminFullName;
        this.studentCount = studentCount;
        this.examCount = examCount;
        this.createdAt = createdAt;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminFullName() {
        return adminFullName;
    }

    public void setAdminFullName(String adminFullName) {
        this.adminFullName = adminFullName;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public int getExamCount() {
        return examCount;
    }

    public void setExamCount(int examCount) {
        this.examCount = examCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String code;
        private String description;
        private String contactEmail;
        private String contactPhone;
        private ApprovalStatus status;
        private String rejectionReason;
        private Long adminUserId;
        private String adminUsername;
        private String adminFullName;
        private int studentCount;
        private int examCount;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder contactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
            return this;
        }

        public Builder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public Builder status(ApprovalStatus status) {
            this.status = status;
            return this;
        }

        public Builder rejectionReason(String rejectionReason) {
            this.rejectionReason = rejectionReason;
            return this;
        }

        public Builder adminUserId(Long adminUserId) {
            this.adminUserId = adminUserId;
            return this;
        }

        public Builder adminUsername(String adminUsername) {
            this.adminUsername = adminUsername;
            return this;
        }

        public Builder adminFullName(String adminFullName) {
            this.adminFullName = adminFullName;
            return this;
        }

        public Builder studentCount(int studentCount) {
            this.studentCount = studentCount;
            return this;
        }

        public Builder examCount(int examCount) {
            this.examCount = examCount;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public InstituteDto build() {
            return new InstituteDto(id, name, code, description, contactEmail, contactPhone,
                    status, rejectionReason, adminUserId, adminUsername, adminFullName,
                    studentCount, examCount, createdAt);
        }
    }
}
