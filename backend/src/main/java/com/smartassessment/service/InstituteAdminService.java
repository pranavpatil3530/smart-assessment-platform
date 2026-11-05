package com.smartassessment.service;

import com.smartassessment.dto.common.ApiResponse;
import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.dto.institute.AssignExamRequestDto;
import com.smartassessment.dto.institute.CandidateDto;
import com.smartassessment.dto.institute.InstituteDto;
import com.smartassessment.security.UserPrincipal;

import java.util.List;
import java.util.Map;

public interface InstituteAdminService {

    InstituteDto getCurrentInstitute(UserPrincipal currentUser);

    PagedResponse<CandidateDto> getInstituteCandidates(UserPrincipal currentUser, int page, int size);

    List<CandidateDto> getExamCandidateAssignments(UserPrincipal currentUser, Long examId);

    ApiResponse assignCandidatesToExam(UserPrincipal currentUser, Long examId, AssignExamRequestDto requestDto);

    Map<String, Object> getInstituteStats(UserPrincipal currentUser);
}
