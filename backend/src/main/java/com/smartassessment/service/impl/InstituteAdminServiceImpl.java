package com.smartassessment.service.impl;

import com.smartassessment.dto.common.ApiResponse;
import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.dto.institute.AssignExamRequestDto;
import com.smartassessment.dto.institute.CandidateDto;
import com.smartassessment.dto.institute.InstituteDto;
import com.smartassessment.entity.Exam;
import com.smartassessment.entity.Institute;
import com.smartassessment.entity.Role;
import com.smartassessment.entity.User;
import com.smartassessment.exception.BadRequestException;
import com.smartassessment.exception.ResourceNotFoundException;
import com.smartassessment.repository.ExamRepository;
import com.smartassessment.repository.InstituteRepository;
import com.smartassessment.repository.UserRepository;
import com.smartassessment.security.UserPrincipal;
import com.smartassessment.service.InstituteAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InstituteAdminServiceImpl implements InstituteAdminService {

    private static final Logger log = LoggerFactory.getLogger(InstituteAdminServiceImpl.class);

    private final InstituteRepository instituteRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    public InstituteAdminServiceImpl(InstituteRepository instituteRepository,
                                     UserRepository userRepository,
                                     ExamRepository examRepository) {
        this.instituteRepository = instituteRepository;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
    }

    private Institute getInstituteForUser(UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        if (user.getInstitute() != null) {
            return user.getInstitute();
        }

        return instituteRepository.findByAdminUser(user)
                .orElseThrow(() -> new BadRequestException("Current user is not associated with any institute."));
    }

    @Override
    @Transactional(readOnly = true)
    public InstituteDto getCurrentInstitute(UserPrincipal currentUser) {
        Institute institute = getInstituteForUser(currentUser);
        return mapToDto(institute);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CandidateDto> getInstituteCandidates(UserPrincipal currentUser, int page, int size) {
        Institute institute = getInstituteForUser(currentUser);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "fullName"));
        Page<User> candidatePage = userRepository.findByInstituteAndRole(institute, Role.ROLE_STUDENT, pageable);

        List<CandidateDto> candidates = candidatePage.getContent().stream()
                .map(u -> new CandidateDto(u.getId(), u.getUsername(), u.getFullName(), u.getEmail(), false))
                .collect(Collectors.toList());

        return new PagedResponse<>(
                candidates,
                candidatePage.getNumber(),
                candidatePage.getSize(),
                candidatePage.getTotalElements(),
                candidatePage.getTotalPages(),
                candidatePage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateDto> getExamCandidateAssignments(UserPrincipal currentUser, Long examId) {
        Institute institute = getInstituteForUser(currentUser);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        if (exam.getInstitute() != null && !exam.getInstitute().getId().equals(institute.getId())) {
            throw new BadRequestException("This examination belongs to another institute.");
        }

        List<User> students = userRepository.findByInstituteAndRole(institute, Role.ROLE_STUDENT);
        Set<Long> assignedIds = exam.getAssignedCandidates().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        boolean assignAll = exam.isAssignAllStudents();

        return students.stream()
                .map(student -> new CandidateDto(
                        student.getId(),
                        student.getUsername(),
                        student.getFullName(),
                        student.getEmail(),
                        assignAll || assignedIds.contains(student.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApiResponse assignCandidatesToExam(UserPrincipal currentUser, Long examId, AssignExamRequestDto requestDto) {
        Institute institute = getInstituteForUser(currentUser);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        if (exam.getInstitute() != null && !exam.getInstitute().getId().equals(institute.getId())) {
            throw new BadRequestException("You cannot manage assignments for an exam belonging to another institute.");
        }

        if (exam.getInstitute() == null) {
            exam.setInstitute(institute);
        }

        if (requestDto.isAssignAll()) {
            exam.setAssignAllStudents(true);
            exam.getAssignedCandidates().clear();
            examRepository.save(exam);
            log.info("Institute admin {} set exam {} to ALL students of institute {}",
                    currentUser.getUsername(), examId, institute.getName());
            return new ApiResponse(true, "Exam assigned to all candidates in " + institute.getName());
        }

        exam.setAssignAllStudents(false);
        exam.getAssignedCandidates().clear();

        if (requestDto.getCandidateIds() != null && !requestDto.getCandidateIds().isEmpty()) {
            List<User> candidates = userRepository.findAllById(requestDto.getCandidateIds());
            for (User candidate : candidates) {
                if (candidate.getInstitute() != null && candidate.getInstitute().getId().equals(institute.getId())) {
                    exam.getAssignedCandidates().add(candidate);
                }
            }
        }

        examRepository.save(exam);
        log.info("Institute admin {} assigned {} specific candidate(s) to exam {}",
                currentUser.getUsername(), exam.getAssignedCandidates().size(), examId);

        return new ApiResponse(true, "Exam assigned to " + exam.getAssignedCandidates().size() + " selected candidate(s).");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getInstituteStats(UserPrincipal currentUser) {
        Institute institute = getInstituteForUser(currentUser);
        Map<String, Object> stats = new HashMap<>();
        stats.put("instituteId", institute.getId());
        stats.put("instituteName", institute.getName());
        stats.put("instituteCode", institute.getCode());
        stats.put("status", institute.getStatus());
        stats.put("rejectionReason", institute.getRejectionReason());
        stats.put("totalCandidates", userRepository.countByInstituteAndRole(institute, Role.ROLE_STUDENT));
        stats.put("totalExams", examRepository.countByInstitute(institute));
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
