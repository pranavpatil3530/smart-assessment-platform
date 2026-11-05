package com.smartassessment.service;

import com.smartassessment.dto.auth.JwtAuthResponse;
import com.smartassessment.dto.auth.LoginRequest;
import com.smartassessment.dto.auth.RegisterRequest;
import com.smartassessment.dto.auth.UserResponseDto;
import com.smartassessment.entity.Role;
import com.smartassessment.entity.User;
import com.smartassessment.exception.BadRequestException;
import com.smartassessment.repository.InstituteRepository;
import com.smartassessment.repository.UserRepository;
import com.smartassessment.security.TokenProvider;
import com.smartassessment.security.UserPrincipal;
import com.smartassessment.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InstituteRepository instituteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("Password123")
                .fullName("New User")
                .role("ROLE_STUDENT")
                .build();

        savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .email("newuser@example.com")
                .password("encoded_pass")
                .fullName("New User")
                .role(Role.ROLE_STUDENT)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new student user")
    void register_Success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getRole()).isEqualTo(Role.ROLE_STUDENT);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException if username is already taken")
    void register_DuplicateUsername_ThrowsBadRequest() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Username is already taken");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully authenticate user and return JWT token")
    void login_Success() {
        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("newuser")
                .password("Password123")
                .build();

        UserPrincipal principal = UserPrincipal.create(savedUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(tokenProvider.generateToken(auth)).thenReturn("mocked.jwt.token");

        JwtAuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getRole()).isEqualTo("ROLE_STUDENT");
    }
}
