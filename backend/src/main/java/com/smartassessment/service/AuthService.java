package com.smartassessment.service;

import com.smartassessment.dto.auth.JwtAuthResponse;
import com.smartassessment.dto.auth.LoginRequest;
import com.smartassessment.dto.auth.RegisterRequest;
import com.smartassessment.dto.auth.UserResponseDto;
import com.smartassessment.security.UserPrincipal;

public interface AuthService {
    JwtAuthResponse login(LoginRequest loginRequest);
    UserResponseDto register(RegisterRequest registerRequest);
    UserResponseDto getCurrentUser(UserPrincipal currentUser);
}
