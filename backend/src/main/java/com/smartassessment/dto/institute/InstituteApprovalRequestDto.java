package com.smartassessment.dto.institute;

import com.smartassessment.entity.ApprovalStatus;
import jakarta.validation.constraints.NotNull;

public class InstituteApprovalRequestDto {

    @NotNull(message = "Approval status must be specified (APPROVED or REJECTED)")
    private ApprovalStatus status;

    private String rejectionReason;

    public InstituteApprovalRequestDto() {
    }

    public InstituteApprovalRequestDto(ApprovalStatus status, String rejectionReason) {
        this.status = status;
        this.rejectionReason = rejectionReason;
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
}
