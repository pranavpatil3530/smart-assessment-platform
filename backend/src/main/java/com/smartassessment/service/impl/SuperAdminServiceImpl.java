package com.smartassessment.service.impl;

import com.smartassessment.dto.common.ApiResponse;
import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.dto.institute.InstituteApprovalRequestDto;
import com.smartassessment.dto.institute.InstituteDto;
import com.smartassessment.entity.ApprovalStatus;
import com.smartassessment.entity.Institute;
import com.smartassessment.entity.Role;
import com.smartassessment.entity.User;
import com.smartassessment.exception.BadRequestException;
import com.smartassessment.exception.ResourceNotFoundException;
import com.smartassessment.repository.ExamRepository;
import com.smartassessment.repository.InstituteRepository;
import com.smartassessment.repository.UserRepository;
import com.smartassessment.service.SuperAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminServiceImpl.class);

    private final InstituteRepository instituteRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    public SuperAdminServiceImpl(InstituteRepository instituteRepository,
                                 UserRepository userRepository,
                                 ExamRepository examRepository) {
        this.instituteRepository = instituteRepository;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InstituteDto> getInstitutes(ApprovalStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Institute> institutes;
        if (status != null) {
            institutes = instituteRepository.findByStatus(status, pageable);
        } else {
            institutes = instituteRepository.findAll(pageable);
        }

        List<InstituteDto> content = institutes.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                institutes.getNumber(),
                institutes.getSize(),
                institutes.getTotalElements(),
                institutes.getTotalPages(),
                institutes.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InstituteDto> getPendingInstitutes(int page, int size) {
        return getInstitutes(ApprovalStatus.PENDING, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public InstituteDto getInstituteById(Long id) {
        Institute institute = instituteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institute", "id", id));
        return mapToDto(institute);
    }

    @Override
    @Transactional
    public ApiResponse approveInstitute(Long id) {
        Institute institute = instituteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institute", "id", id));

        institute.setStatus(ApprovalStatus.APPROVED);
        institute.setRejectionReason(null);

        User admin = institute.getAdminUser();
        if (admin != null) {
            admin.setApprovalStatus(ApprovalStatus.APPROVED);
            admin.setEnabled(true);
            userRepository.save(admin);
        }

        instituteRepository.save(institute);
        log.info("Super admin approved institute: {} (ID: {}) and enabled admin user: {}",
                institute.getName(), institute.getId(), admin != null ? admin.getUsername() : "none");

        return new ApiResponse(true, "Institute '" + institute.getName() + "' approved successfully! The Institute Admin can now sign in.");
    }

    @Override
    @Transactional
    public ApiResponse rejectInstitute(Long id, InstituteApprovalRequestDto requestDto) {
        Institute institute = instituteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institute", "id", id));

        String reason = (requestDto != null && requestDto.getReason() != null && !requestDto.getReason().trim().isEmpty())
                ? requestDto.getReason().trim()
                : "Application did not meet registration criteria.";

        institute.setStatus(ApprovalStatus.REJECTED);
        institute.setRejectionReason(reason);

        User admin = institute.getAdminUser();
        if (admin != null) {
            admin.setApprovalStatus(ApprovalStatus.REJECTED);
            admin.setEnabled(false);
            userRepository.save(admin);
        }

        instituteRepository.save(institute);
        log.info("Super admin rejected institute: {} (ID: {}). Reason: {}",
                institute.getName(), institute.getId(), reason);

        return new ApiResponse(true, "Institute '" + institute.getName() + "' has been rejected.");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSuperAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalInstitutes", instituteRepository.count());
        stats.put("pendingInstitutes", instituteRepository.countByStatus(ApprovalStatus.PENDING));
        stats.put("approvedInstitutes", instituteRepository.countByStatus(ApprovalStatus.APPROVED));
        stats.put("rejectedInstitutes", instituteRepository.countByStatus(ApprovalStatus.REJECTED));
        stats.put("totalStudents", userRepository.countByRole(Role.ROLE_STUDENT));
        stats.put("totalExams", examRepository.count());
        return stats;
    }

    private InstituteDto mapToDto(Institute institute) {
        int studentCount = (int) userRepository.countByInstituteAndRole(institute, Role.ROLE_STUDENT);
        int examCount = (int) examRepository.countByInstitute(institute);

        return InstituteDto.builder()
                .id(institute.getId())
                .name(institute.getName())
                .code(institute.getCode())
                .description(institute.getDescription())
                .contactEmail(institute.getContactEmail())
                .contactPhone(institute.getContactPhone())
                .status(institute.getStatus())
                .rejectionReason(institute.getRejectionReason())
                .adminUserId(institute.getAdminUser() != null ? institute.getAdminUser().getId() : null)
                .adminUsername(institute.getAdminUser() != null ? institute.getAdminUser().getUsername() : null)
                .adminFullName(institute.getAdminUser() != null ? institute.getAdminUser().getFullName() : null)
                .studentCount(studentCount)
                .examCount(examCount)
                .createdAt(institute.getCreatedAt())
                .build();
    }
}
