package com.smartassessment.controller;

import com.smartassessment.dto.common.ApiResponse;
import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.dto.institute.AssignExamRequestDto;
import com.smartassessment.dto.institute.CandidateDto;
import com.smartassessment.dto.institute.InstituteDto;
import com.smartassessment.security.CurrentUser;
import com.smartassessment.security.UserPrincipal;
import com.smartassessment.service.InstituteAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/institute")
@PreAuthorize("hasAnyAuthority('ROLE_INSTITUTE_ADMIN', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
public class InstituteAdminController {

    private final InstituteAdminService instituteAdminService;

    public InstituteAdminController(InstituteAdminService instituteAdminService) {
        this.instituteAdminService = instituteAdminService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<InstituteDto>> getCurrentInstitute(@CurrentUser UserPrincipal currentUser) {
        InstituteDto institute = instituteAdminService.getCurrentInstitute(currentUser);
        return ResponseEntity.ok(ApiResponse.success(institute, "Institute details retrieved successfully"));
    }

    @GetMapping("/candidates")
    public ResponseEntity<ApiResponse<PagedResponse<CandidateDto>>> getInstituteCandidates(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<CandidateDto> candidates = instituteAdminService.getInstituteCandidates(currentUser, page, size);
        return ResponseEntity.ok(ApiResponse.success(candidates, "Candidates retrieved successfully"));
    }

    @GetMapping("/exams/{examId}/candidates")
    public ResponseEntity<ApiResponse<List<CandidateDto>>> getExamCandidateAssignments(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long examId) {
        List<CandidateDto> candidates = instituteAdminService.getExamCandidateAssignments(currentUser, examId);
        return ResponseEntity.ok(ApiResponse.success(candidates, "Exam candidate assignments retrieved successfully"));
    }

    @PostMapping("/exams/{examId}/assign")
    public ResponseEntity<ApiResponse<Void>> assignCandidatesToExam(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long examId,
            @RequestBody AssignExamRequestDto requestDto) {
        ApiResponse response = instituteAdminService.assignCandidatesToExam(currentUser, examId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(null, response.getMessage()));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInstituteStats(@CurrentUser UserPrincipal currentUser) {
        Map<String, Object> stats = instituteAdminService.getInstituteStats(currentUser);
        return ResponseEntity.ok(ApiResponse.success(stats, "Institute statistics retrieved successfully"));
    }
}
