package com.smartassessment.service.impl;

import com.smartassessment.dto.auth.JwtAuthResponse;
import com.smartassessment.dto.auth.LoginRequest;
import com.smartassessment.dto.auth.RegisterRequest;
import com.smartassessment.dto.auth.UserResponseDto;
import com.smartassessment.entity.ApprovalStatus;
import com.smartassessment.entity.Institute;
import com.smartassessment.entity.Role;
import com.smartassessment.entity.User;
import com.smartassessment.exception.BadRequestException;
import com.smartassessment.exception.ResourceNotFoundException;
import com.smartassessment.repository.InstituteRepository;
import com.smartassessment.repository.UserRepository;
import com.smartassessment.security.TokenProvider;
import com.smartassessment.security.UserPrincipal;
import com.smartassessment.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final InstituteRepository instituteRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           InstituteRepository instituteRepository,
                           PasswordEncoder passwordEncoder,
                           TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.instituteRepository = instituteRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public JwtAuthResponse login(LoginRequest loginRequest) {
        log.info("Attempting authentication for user: {}", loginRequest.getUsernameOrEmail());

        User preCheckUser = userRepository.findByUsernameOrEmail(
                loginRequest.getUsernameOrEmail().trim(),
                loginRequest.getUsernameOrEmail().trim().toLowerCase()
        ).orElse(null);

        if (preCheckUser != null && preCheckUser.getRole() == Role.ROLE_INSTITUTE_ADMIN) {
            if (preCheckUser.getApprovalStatus() == ApprovalStatus.PENDING) {
                throw new BadRequestException("Your Institute Admin registration is pending approval by the Main Admin. You will be able to sign in once approved.");
            } else if (preCheckUser.getApprovalStatus() == ApprovalStatus.REJECTED) {
                String reason = (preCheckUser.getInstitute() != null && preCheckUser.getInstitute().getRejectionReason() != null)
                        ? ": " + preCheckUser.getInstitute().getRejectionReason()
                        : ".";
                throw new BadRequestException("Your Institute Admin registration was rejected by the Main Admin" + reason);
            }
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        Long instituteId = user.getInstitute() != null ? user.getInstitute().getId() : null;
        String instituteName = user.getInstitute() != null ? user.getInstitute().getName() : null;

        return JwtAuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .userId(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .email(userPrincipal.getEmail())
                .fullName(userPrincipal.getFullName())
                .role(user.getRole().name())
                .instituteId(instituteId)
                .instituteName(instituteName)
                .approvalStatus(user.getApprovalStatus() != null ? user.getApprovalStatus().name() : null)
                .build();
    }

    @Override
    @Transactional
    public UserResponseDto register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email address already in use!");
        }

        Role assignedRole = registerRequest.getRole() != null ? registerRequest.getRole() : Role.ROLE_STUDENT;

        if (assignedRole == Role.ROLE_SUPER_ADMIN || assignedRole == Role.ROLE_ADMIN) {
            throw new BadRequestException("Registration for Super Admin or Admin role is not allowed.");
        }

        if (assignedRole == Role.ROLE_INSTITUTE_ADMIN) {
            if (registerRequest.getInstituteName() == null || registerRequest.getInstituteName().trim().isEmpty()) {
                throw new BadRequestException("Institute name is required for Institute Admin registration.");
            }

            String code = registerRequest.getInstituteCode();
            if (code == null || code.trim().isEmpty()) {
                code = generateInstituteCode(registerRequest.getInstituteName());
            } else {
                code = code.trim().toUpperCase();
                if (instituteRepository.existsByCode(code)) {
                    throw new BadRequestException("Institute code '" + code + "' is already in use. Please choose another code.");
                }
            }

            Institute institute = Institute.builder()
                    .name(registerRequest.getInstituteName().trim())
                    .code(code)
                    .contactEmail(registerRequest.getEmail().trim().toLowerCase())
                    .contactPhone(registerRequest.getContactPhone() != null ? registerRequest.getContactPhone().trim() : null)
                    .status(ApprovalStatus.PENDING)
                    .build();
            Institute savedInstitute = instituteRepository.save(institute);

            User user = User.builder()
                    .username(registerRequest.getUsername().trim())
                    .email(registerRequest.getEmail().trim().toLowerCase())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .fullName(registerRequest.getFullName().trim())
                    .role(Role.ROLE_INSTITUTE_ADMIN)
                    .enabled(false) // Disabled until approved by Main Admin
                    .approvalStatus(ApprovalStatus.PENDING)
                    .institute(savedInstitute)
                    .build();

            User savedUser = userRepository.save(user);
            savedInstitute.setAdminUser(savedUser);
            instituteRepository.save(savedInstitute);

            log.info("Registered new institute admin: {} for institute: {} (PENDING APPROVAL)",
                    savedUser.getUsername(), institute.getName());

            return mapToUserResponse(savedUser);
        }

        // ROLE_STUDENT registration
        Institute studentInstitute = null;
        if (registerRequest.getInstituteCode() != null && !registerRequest.getInstituteCode().trim().isEmpty()) {
            String code = registerRequest.getInstituteCode().trim().toUpperCase();
            studentInstitute = instituteRepository.findByCode(code)
                    .orElseThrow(() -> new BadRequestException("Institute with code '" + code + "' not found."));

            if (studentInstitute.getStatus() != ApprovalStatus.APPROVED) {
                throw new BadRequestException("Institute '" + studentInstitute.getName() + "' is not approved yet.");
            }
        }

        User user = User.builder()
                .username(registerRequest.getUsername().trim())
                .email(registerRequest.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName().trim())
                .role(Role.ROLE_STUDENT)
                .enabled(true)
                .approvalStatus(ApprovalStatus.APPROVED)
                .institute(studentInstitute)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered new student: {} with institute: {}",
                savedUser.getUsername(), studentInstitute != null ? studentInstitute.getName() : "None");

        return mapToUserResponse(savedUser);
    }

    private String generateInstituteCode(String name) {
        String clean = name.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        String prefix = clean.length() > 5 ? clean.substring(0, 5) : clean;
        String code = prefix + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        while (instituteRepository.existsByCode(code)) {
            code = prefix + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        }
        return code;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser(UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        return mapToUserResponse(user);
    }

    private UserResponseDto mapToUserResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .instituteId(user.getInstitute() != null ? user.getInstitute().getId() : null)
                .instituteName(user.getInstitute() != null ? user.getInstitute().getName() : null)
                .approvalStatus(user.getApprovalStatus() != null ? user.getApprovalStatus().name() : null)
                .build();
    }
}
