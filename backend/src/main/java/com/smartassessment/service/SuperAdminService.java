package com.smartassessment.service;

import com.smartassessment.dto.common.ApiResponse;
import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.dto.institute.InstituteApprovalRequestDto;
import com.smartassessment.dto.institute.InstituteDto;
import com.smartassessment.entity.ApprovalStatus;

import java.util.Map;

public interface SuperAdminService {

    PagedResponse<InstituteDto> getInstitutes(ApprovalStatus status, int page, int size);

    PagedResponse<InstituteDto> getPendingInstitutes(int page, int size);

    InstituteDto getInstituteById(Long id);

    ApiResponse approveInstitute(Long id);

    ApiResponse rejectInstitute(Long id, InstituteApprovalRequestDto requestDto);

    Map<String, Object> getSuperAdminStats();
}
