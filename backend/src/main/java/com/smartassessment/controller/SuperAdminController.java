package com.smartassessment.controller;

import com.smartassessment.dto.common.ApiResponse;
import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.dto.institute.InstituteApprovalRequestDto;
import com.smartassessment.dto.institute.InstituteDto;
import com.smartassessment.entity.ApprovalStatus;
import com.smartassessment.service.SuperAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/super-admin")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    @GetMapping("/institutes")
    public ResponseEntity<ApiResponse<PagedResponse<InstituteDto>>> getInstitutes(
            @RequestParam(required = false) ApprovalStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<InstituteDto> institutes = superAdminService.getInstitutes(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(institutes, "Institutes retrieved successfully"));
    }

    @GetMapping("/institutes/pending")
    public ResponseEntity<ApiResponse<PagedResponse<InstituteDto>>> getPendingInstitutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<InstituteDto> pending = superAdminService.getPendingInstitutes(page, size);
        return ResponseEntity.ok(ApiResponse.success(pending, "Pending institutes retrieved successfully"));
    }

    @GetMapping("/institutes/{id}")
    public ResponseEntity<ApiResponse<InstituteDto>> getInstituteById(@PathVariable Long id) {
        InstituteDto institute = superAdminService.getInstituteById(id);
        return ResponseEntity.ok(ApiResponse.success(institute, "Institute details retrieved successfully"));
    }

    @PutMapping("/institutes/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveInstitute(@PathVariable Long id) {
        ApiResponse response = superAdminService.approveInstitute(id);
        return ResponseEntity.ok(ApiResponse.success(null, response.getMessage()));
    }

    @PutMapping("/institutes/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectInstitute(
            @PathVariable Long id,
            @RequestBody(required = false) InstituteApprovalRequestDto requestDto) {
        ApiResponse response = superAdminService.rejectInstitute(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success(null, response.getMessage()));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSuperAdminStats() {
        Map<String, Object> stats = superAdminService.getSuperAdminStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Super admin statistics retrieved successfully"));
    }
}
