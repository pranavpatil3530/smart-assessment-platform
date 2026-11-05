package com.smartassessment.dto.institute;

import java.util.ArrayList;
import java.util.List;

public class AssignExamRequestDto {

    private boolean assignAll = false;
    private List<Long> candidateIds = new ArrayList<>();

    public AssignExamRequestDto() {
    }

    public AssignExamRequestDto(boolean assignAll, List<Long> candidateIds) {
        this.assignAll = assignAll;
        this.candidateIds = candidateIds != null ? candidateIds : new ArrayList<>();
    }

    public boolean isAssignAll() {
        return assignAll;
    }

    public void setAssignAll(boolean assignAll) {
        this.assignAll = assignAll;
    }

    public List<Long> getCandidateIds() {
        return candidateIds;
    }

    public void setCandidateIds(List<Long> candidateIds) {
        this.candidateIds = candidateIds;
    }
}
